package ru.hh.plugins.geminio.wizard

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import ru.hh.plugins.extensions.isQualifiedPackageName
import ru.hh.plugins.extensions.isValidIdentifier
import ru.hh.plugins.extensions.layout.onTextChange
import ru.hh.plugins.geminio.sdk.form.GeminioForm
import ru.hh.plugins.geminio.sdk.form.GeminioFormField
import ru.hh.plugins.geminio.sdk.form.GeminioFormFieldOrigin
import ru.hh.plugins.geminio.sdk.form.GeminioFormSession
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator

/**
 * Simple custom dialog for rendering Geminio forms without `ModelWizard`.
 *
 * The dialog is built with Kotlin UI DSL, keeps mutable values in [GeminioFormSession] and
 * eagerly refreshes dependent fields such as `suggest`-driven globals.
 */
internal class GeminioFormDialog(
    private val project: Project,
    title: String,
    private val templateName: String,
    private val templateDescription: String,
    private val form: GeminioForm,
    private val session: GeminioFormSession,
    private val headerIcon: Icon = AllIcons.Nodes.Module,
) : DialogWrapper(project, true) {

    private val autoManagedStringFieldIds = linkedSetOf<String>()
    private val autoManagedBooleanFieldIds = linkedSetOf<String>()
    private val fieldRows = linkedMapOf<String, Row>()
    private val fieldVisibility = linkedMapOf<String, Boolean>()
    private val stringFields = linkedMapOf<String, Cell<JBTextField>>()
    private val booleanFields = linkedMapOf<String, Cell<JBCheckBox>>()
    private val stringFieldLayouts = form.computeStringFieldLayouts()

    private lateinit var contentPanel: DialogPanel

    private var isRefreshing = false

    init {
        this.title = title
        isResizable = true
        init()
        refreshUi()
    }

    override fun createCenterPanel(): JComponent {
        contentPanel = panel {
            form.fields.forEach { field ->
                when (field) {
                    is GeminioFormField.StringField -> createStringFieldRow(field)
                    is GeminioFormField.BooleanField -> createBooleanFieldRow(field)
                }
            }
        }.apply {
            border = JBUI.Borders.empty(12, 12, 12, 20)
        }

        return JBScrollPane(contentPanel).apply {
            preferredSize = Dimension(JBUI.scale(560), JBUI.scale(420))
            border = JBUI.Borders.empty()
        }.let { scrollPane ->
            JPanel(BorderLayout()).apply {
                add(createHeaderPanel(), BorderLayout.NORTH)
                add(scrollPane, BorderLayout.CENTER)
            }
        }
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return stringFields.entries.firstOrNull { (fieldId, _) -> fieldVisibility[fieldId] != false }?.value?.component
            ?: booleanFields.entries.firstOrNull { (fieldId, _) -> fieldVisibility[fieldId] != false }?.value?.component
    }

    override fun doValidateAll(): List<ValidationInfo> {
        return form.fields.mapNotNull { field ->
            val state = session.fieldState(field.id)
            if ((fieldVisibility[field.id] ?: true).not() || state.enabled.not()) {
                return@mapNotNull null
            }

            when (field) {
                is GeminioFormField.StringField -> validateStringField(field)
                is GeminioFormField.BooleanField -> null
            }
        }
    }

    private fun Panel.createStringFieldRow(field: GeminioFormField.StringField) {
        val initialText = session.stringValue(field.id).orEmpty()

        val row = row("${field.name}:") {
            val textFieldCell = textField()
                .align(Align.FILL)
                .resizableColumn()
                .applyToComponent {
                    text = initialText
                    onTextChange {
                        if (isRefreshing) {
                            return@onTextChange
                        }

                        // Preserve empty string as a real value so dependent expressions can
                        // react to deleting the last character without falling back to `null`.
                        session.setStringValue(field.id, text)
                        autoManagedStringFieldIds.remove(field.id)
                        refreshUi()
                    }
                }

            field.help
                ?.takeIf { shouldShowFieldComment(field.name, it) }
                ?.let(textFieldCell::comment)
            stringFields[field.id] = textFieldCell
        }
        if (stringFieldLayouts[field.id] == RowLayout.INDEPENDENT) {
            row.layout(RowLayout.INDEPENDENT)
        }

        fieldRows[field.id] = row
        fieldVisibility[field.id] = true

        if (field.suggestEvaluator != null && shouldAutoManageStringField(field.id, initialText)) {
            autoManagedStringFieldIds += field.id
        }
    }

    private fun createHeaderPanel(): JComponent {
        val titleLabel = JLabel(templateName).apply {
            font = font.deriveFont(Font.BOLD, font.size2D + JBUI.scale(6).toFloat())
        }
        val descriptionLabel = JLabel(
            "<html>${StringUtil.escapeXmlEntities(templateDescription)}</html>"
        ).apply {
            foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND
        }

        val textPanel = JPanel().apply {
            isOpaque = false
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(titleLabel)
            add(Box.createVerticalStrut(JBUI.scale(4)))
            add(descriptionLabel)
        }

        val content = JPanel(BorderLayout(JBUI.scale(12), 0)).apply {
            border = JBUI.Borders.empty(16)
            add(JLabel(headerIcon).apply {
                verticalAlignment = JLabel.TOP
            }, BorderLayout.WEST)
            add(textPanel, BorderLayout.CENTER)
        }

        return JPanel(BorderLayout()).apply {
            add(content, BorderLayout.CENTER)
            add(JSeparator(), BorderLayout.SOUTH)
        }
    }

    private fun Panel.createBooleanFieldRow(field: GeminioFormField.BooleanField) {
        val initialValue = session.booleanValue(field.id)

        val row = row {
            val checkBoxCell = checkBox(field.name)
                .applyToComponent {
                    isSelected = initialValue
                    addActionListener {
                        if (isRefreshing) {
                            return@addActionListener
                        }

                        session.setBooleanValue(field.id, isSelected)
                        if (field.origin == GeminioFormFieldOrigin.GLOBAL) {
                            autoManagedBooleanFieldIds.remove(field.id)
                        }
                        refreshUi()
                    }
                }

            field.help
                ?.takeIf { shouldShowFieldComment(field.name, it) }
                ?.let(checkBoxCell::comment)
            booleanFields[field.id] = checkBoxCell
        }

        fieldRows[field.id] = row
        fieldVisibility[field.id] = true

        if (field.origin == GeminioFormFieldOrigin.GLOBAL && field.initialValueEvaluator != null) {
            autoManagedBooleanFieldIds += field.id
        }
    }

    private fun refreshUi() {
        isRefreshing = true
        var visibilityChanged = false
        try {
            syncAutoManagedValues()

            form.fields.forEach { field ->
                val state = session.fieldState(field.id)
                val previousVisibility = fieldVisibility.put(field.id, state.visible)

                if (previousVisibility != state.visible) {
                    fieldRows[field.id]?.visible(state.visible)
                    visibilityChanged = true
                }

                fieldRows[field.id]?.enabled(state.enabled)

                when (field) {
                    is GeminioFormField.StringField -> {
                        stringFields[field.id]?.component?.apply {
                            val expectedText = session.stringValue(field.id).orEmpty()
                            if (text != expectedText) {
                                text = expectedText
                            }
                        }
                    }

                    is GeminioFormField.BooleanField -> {
                        booleanFields[field.id]?.component?.apply {
                            val expectedSelected = session.booleanValue(field.id)
                            if (isSelected != expectedSelected) {
                                isSelected = expectedSelected
                            }
                        }
                    }
                }
            }
        } finally {
            isRefreshing = false
        }

        if (visibilityChanged) {
            contentPanel.revalidate()
            contentPanel.repaint()
        }
    }

    private fun syncAutoManagedValues() {
        autoManagedStringFieldIds.forEach { fieldId ->
            val suggestedValue = session.suggestedStringValue(fieldId).orEmpty()
            session.setStringValue(fieldId, suggestedValue)
        }

        autoManagedBooleanFieldIds.forEach { fieldId ->
            val field = form.requireField(fieldId) as? GeminioFormField.BooleanField ?: return@forEach
            val value = field.initialValueEvaluator?.invoke(session) ?: session.booleanValue(fieldId)
            session.setBooleanValue(fieldId, value)
        }
    }

    private fun shouldAutoManageStringField(
        fieldId: String,
        currentText: String,
    ): Boolean {
        val suggestedValue = session.suggestedStringValue(fieldId).orEmpty()

        return currentText.isEmpty() || currentText == suggestedValue
    }

    private fun validateStringField(field: GeminioFormField.StringField): ValidationInfo? {
        val value = session.stringValue(field.id).orEmpty()
        val component = stringFields[field.id]?.component ?: return null

        field.constraints.forEach { constraint ->
            when (constraint) {
                StringParameterConstraint.NONEMPTY -> {
                    if (value.isBlank()) {
                        return ValidationInfo("'${field.name}' should not be empty", component)
                    }
                }

                StringParameterConstraint.CLASS -> {
                    if (value.isNotBlank() && value.isValidIdentifier(project).not()) {
                        return ValidationInfo("'${field.name}' should be a valid class name", component)
                    }
                }

                StringParameterConstraint.PACKAGE -> {
                    if (value.isNotBlank() && value.isQualifiedPackageName(project).not()) {
                        return ValidationInfo("'${field.name}' should be a valid package name", component)
                    }
                }

                StringParameterConstraint.MODULE -> {
                    if (value.isBlank() || value.any { it.isWhitespace() } || value.contains(':') || value.contains('/')) {
                        return ValidationInfo("'${field.name}' should be a valid module name", component)
                    }
                }

                StringParameterConstraint.SOURCE_SET_FOLDER -> {
                    if (value.isBlank() || value.any { it == '/' || it == '\\' || it.isWhitespace() }) {
                        return ValidationInfo("'${field.name}' should be a valid source set name", component)
                    }
                }

                else -> {
                    // Other constraints still live in the old Android template runtime and will be
                    // reintroduced explicitly once the custom UI/execution stack fully replaces it.
                }
            }
        }

        return null
    }

    private fun shouldShowFieldComment(
        fieldName: String,
        help: String,
    ): Boolean {
        return help.isNotBlank() && help != fieldName
    }
}

private fun GeminioForm.computeStringFieldLayouts(): Map<String, RowLayout> {
    val layouts = linkedMapOf<String, RowLayout>()
    fields
        .splitIntoNeighboringStringGroups()
        .forEach { stringFields ->
            val shortestLabelLength = stringFields.minOfOrNull { it.name.length } ?: return@forEach

            stringFields.forEach { field ->
                layouts[field.id] = if (field.name.length >= shortestLabelLength * 2 && shortestLabelLength > 0) {
                    RowLayout.INDEPENDENT
                } else {
                    RowLayout.LABEL_ALIGNED
                }
            }
        }

    return layouts
}

private fun List<GeminioFormField>.splitIntoNeighboringStringGroups(): List<List<GeminioFormField.StringField>> {
    val groups = mutableListOf<List<GeminioFormField.StringField>>()
    val currentGroup = mutableListOf<GeminioFormField.StringField>()

    forEach { field ->
        when (field) {
            is GeminioFormField.StringField -> currentGroup += field
            is GeminioFormField.BooleanField -> {
                if (currentGroup.isNotEmpty()) {
                    groups += currentGroup.toList()
                    currentGroup.clear()
                }
            }
        }
    }

    if (currentGroup.isNotEmpty()) {
        groups += currentGroup.toList()
    }

    return groups
}
