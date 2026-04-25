package ru.hh.plugins.geminio.wizard

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import ru.hh.plugins.geminio.common.extensions.isQualifiedPackageName
import ru.hh.plugins.geminio.common.extensions.isValidIdentifier
import ru.hh.plugins.geminio.sdk.form.GeminioForm
import ru.hh.plugins.geminio.sdk.form.GeminioFormField
import ru.hh.plugins.geminio.sdk.form.GeminioFormFieldOrigin
import ru.hh.plugins.geminio.sdk.form.GeminioFormSession
import ru.hh.plugins.geminio.sdk.form.GeminioStringConstraintValidationContext
import ru.hh.plugins.geminio.sdk.form.GeminioStringConstraintValidator
import ru.hh.plugins.geminio.ui.extensions.onTextChange
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.KeyboardFocusManager
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
    private val confirmActionText: String = "Finish",
    private val preferredScrollSize: Dimension = Dimension(DEFAULT_DIALOG_WIDTH, DEFAULT_DIALOG_HEIGHT),
    private val preferInitialInputFocus: Boolean = true,
    private val validationContextProvider: () -> GeminioStringConstraintValidationContext = {
        GeminioStringConstraintValidationContext()
    },
) : DialogWrapper(project, true) {

    private companion object {
        const val DEFAULT_DIALOG_WIDTH = 560
        const val DEFAULT_DIALOG_HEIGHT = 420
        const val CONTENT_TOP_PADDING = 12
        const val CONTENT_LEFT_PADDING = 12
        const val CONTENT_BOTTOM_PADDING = 12
        const val CONTENT_RIGHT_PADDING = 20
        const val HEADER_TITLE_FONT_DELTA = 6
        const val HEADER_DESCRIPTION_GAP = 4
        const val HEADER_CONTENT_GAP = 12
        const val HEADER_PADDING = 16
    }

    private val autoManagedStringFieldIds = linkedSetOf<String>()
    private val autoManagedBooleanFieldIds = linkedSetOf<String>()
    private val autoManagedSuggestFieldIds = linkedSetOf<String>()
    private val fieldRows = linkedMapOf<String, Row>()
    private val fieldVisibility = linkedMapOf<String, Boolean>()
    private val stringFields = linkedMapOf<String, Cell<JBTextField>>()
    private val booleanFields = linkedMapOf<String, Cell<JBCheckBox>>()
    private val suggestFields = linkedMapOf<String, Cell<GeminioSearchableSuggestField>>()

    private var contentPanel: DialogPanel? = null

    private var isRefreshing = false
    private var isClosingScheduled = false

    init {
        this.title = title
        isResizable = true
        setOKButtonText(confirmActionText)
        init()
        refreshUi()
        initValidation()
    }

    override fun createCenterPanel(): JComponent {
        contentPanel = panel {
            form.fields.forEach { field ->
                when (field) {
                    is GeminioFormField.StringField -> createStringFieldRow(field)
                    is GeminioFormField.BooleanField -> createBooleanFieldRow(field)
                    is GeminioFormField.SuggestField -> createSuggestFieldRow(field)
                }
            }
        }.apply {
            border = JBUI.Borders.empty(
                CONTENT_TOP_PADDING,
                CONTENT_LEFT_PADDING,
                CONTENT_BOTTOM_PADDING,
                CONTENT_RIGHT_PADDING,
            )
        }

        return JBScrollPane(contentPanel).apply {
            preferredSize = Dimension(
                JBUI.scale(preferredScrollSize.width),
                JBUI.scale(preferredScrollSize.height),
            )
            border = JBUI.Borders.empty()
        }.let { scrollPane ->
            JPanel(BorderLayout()).apply {
                add(createHeaderPanel(), BorderLayout.NORTH)
                add(scrollPane, BorderLayout.CENTER)
            }
        }
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        if (preferInitialInputFocus.not()) {
            return null
        }

        return stringFields.entries.firstOrNull { (fieldId, _) -> fieldVisibility[fieldId] != false }?.value?.component
            ?: suggestFields.entries.firstOrNull { (fieldId, _) -> fieldVisibility[fieldId] != false }?.value?.component
            ?: booleanFields.entries.firstOrNull { (fieldId, _) -> fieldVisibility[fieldId] != false }?.value?.component
    }

    override fun doOKAction() {
        if (isClosingScheduled) {
            return
        }

        if (confirmActionText != "Next") {
            super.doOKAction()
            return
        }

        isClosingScheduled = true

        // On macOS/JBR, closing a dialog with an active text caret and opening the next dialog
        // immediately can crash inside native text-input handling. We move focus away from the
        // editor and postpone the actual close to the next EDT tick.
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner()
        rootPane.requestFocusInWindow()

        ApplicationManager.getApplication().invokeLater {
            if (isDisposed.not()) {
                super.doOKAction()
            }
            isClosingScheduled = false
        }
    }

    override fun doValidateAll(): List<ValidationInfo> {
        return form.fields
            .asSequence()
            .filter(::shouldValidateField)
            .mapNotNull(::validateField)
            .toList()
    }

    private fun Panel.createStringFieldRow(field: GeminioFormField.StringField) {
        val initialText = session.stringValue(field.id).orEmpty()
        val row = row {
            createStringFieldCell(field, initialText)
        }
        val textFieldCell = stringFields.getValue(field.id)

        field.help
            ?.takeIf { shouldShowFieldComment(field.name, it) }
            ?.let(textFieldCell::comment)

        fieldRows[field.id] = row
        fieldVisibility[field.id] = true

        if (field.suggestEvaluator != null && shouldAutoManageStringField(field.id, initialText)) {
            autoManagedStringFieldIds += field.id
        }
    }

    private fun Row.createStringFieldCell(
        field: GeminioFormField.StringField,
        initialText: String,
    ): Cell<JBTextField> {
        return textField()
            .align(Align.FILL)
            .resizableColumn()
            .label(field.name, LabelPosition.TOP)
            .applyToComponent {
                text = initialText
                onTextChange {
                    if (isRefreshing.not()) {
                        // Preserve empty string as a real value so dependent expressions can
                        // react to deleting the last character without falling back to `null`.
                        session.setStringValue(field.id, text)
                        autoManagedStringFieldIds.remove(field.id)
                        refreshUi()
                    }
                }
            }
            .also { stringFields[field.id] = it }
    }

    private fun createHeaderPanel(): JComponent {
        val titleLabel = JLabel(templateName).apply {
            font = font.deriveFont(Font.BOLD, font.size2D + JBUI.scale(HEADER_TITLE_FONT_DELTA).toFloat())
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
            add(Box.createVerticalStrut(JBUI.scale(HEADER_DESCRIPTION_GAP)))
            add(descriptionLabel)
        }

        val content = JPanel(BorderLayout(JBUI.scale(HEADER_CONTENT_GAP), 0)).apply {
            border = JBUI.Borders.empty(HEADER_PADDING)
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
                        if (isRefreshing.not()) {
                            session.setBooleanValue(field.id, isSelected)
                            if (field.origin == GeminioFormFieldOrigin.GLOBAL) {
                                autoManagedBooleanFieldIds.remove(field.id)
                            }
                            refreshUi()
                        }
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

    private fun Panel.createSuggestFieldRow(field: GeminioFormField.SuggestField) {
        val initialValue = session.suggestValue(field.id)
        val row = row {
            val suggestFieldCell = cell(
                GeminioSearchableSuggestField(
                    project = project,
                    options = field.options,
                    initialValue = initialValue,
                    isSealed = field.isSealed,
                )
            )
                .align(Align.FILL)
                .resizableColumn()
                .label(field.name, LabelPosition.TOP)
                .applyToComponent {
                    addValueListener {
                        if (isRefreshing.not()) {
                            autoManagedSuggestFieldIds.remove(field.id)
                            val canUpdateSessionValue = field.isSealed.not() || isValidValue()
                            if (canUpdateSessionValue) {
                                session.setSuggestValue(field.id, resolvedValue())
                                refreshUi()
                            }
                        }
                    }
                }

            field.help
                ?.takeIf { shouldShowFieldComment(field.name, it) }
                ?.let(suggestFieldCell::comment)

            suggestFields[field.id] = suggestFieldCell
        }

        fieldRows[field.id] = row
        fieldVisibility[field.id] = true

        if (field.suggestEvaluator != null && shouldAutoManageSuggestField(field, initialValue)) {
            autoManagedSuggestFieldIds += field.id
        }
    }

    private fun refreshUi() {
        isRefreshing = true
        var visibilityChanged = false
        try {
            syncAutoManagedValues()

            form.fields.forEach { field ->
                if (refreshFieldUi(field)) {
                    visibilityChanged = true
                }
            }
        } finally {
            isRefreshing = false
        }

        if (visibilityChanged) {
            contentPanel?.let { panel ->
                panel.revalidate()
                panel.repaint()
            }
        }
    }

    private fun syncAutoManagedValues() {
        autoManagedStringFieldIds.forEach { fieldId ->
            val suggestedValue = session.suggestedStringValue(fieldId).orEmpty()
            session.setStringValue(fieldId, suggestedValue)
        }

        autoManagedBooleanFieldIds.forEach { fieldId ->
            syncAutoManagedBooleanValue(fieldId)
        }

        autoManagedSuggestFieldIds.forEach(session::applySuggestFieldSuggestion)
    }

    private fun shouldAutoManageStringField(
        fieldId: String,
        currentText: String,
    ): Boolean {
        val suggestedValue = session.suggestedStringValue(fieldId).orEmpty()

        return currentText.isEmpty() || currentText == suggestedValue
    }

    private fun shouldAutoManageSuggestField(
        field: GeminioFormField.SuggestField,
        currentValue: String?,
    ): Boolean {
        val suggestedValue = session.suggestedSuggestValue(field.id)
        val defaultValue = field.defaultValue
            ?.takeIf { value -> field.isSealed.not() || field.containsValue(value) }
            ?: field.options.first().value

        return currentValue == suggestedValue || currentValue == defaultValue
    }

    private fun validateStringField(field: GeminioFormField.StringField): ValidationInfo? {
        val value = session.stringValue(field.id).orEmpty()
        val component = stringFields[field.id]?.component ?: return null

        return GeminioStringConstraintValidator.validate(
            field = field,
            value = value,
            context = validationContextProvider(),
            isValidIdentifier = { candidate -> candidate.isValidIdentifier(project) },
            isQualifiedName = { candidate -> candidate.isQualifiedPackageName(project) },
        )?.let { message -> ValidationInfo(message, component) }
    }

    private fun shouldShowFieldComment(
        fieldName: String,
        help: String,
    ): Boolean = help.isNotBlank() && help != fieldName

    private fun shouldValidateField(field: GeminioFormField): Boolean {
        val state = session.fieldState(field.id)
        return (fieldVisibility[field.id] ?: true) && state.enabled
    }

    private fun validateField(field: GeminioFormField): ValidationInfo? {
        return when (field) {
            is GeminioFormField.StringField -> validateStringField(field)
            is GeminioFormField.BooleanField -> null
            is GeminioFormField.SuggestField -> validateSuggestField(field)
        }
    }

    private fun refreshFieldUi(field: GeminioFormField): Boolean {
        val state = session.fieldState(field.id)
        val previousVisibility = fieldVisibility.put(field.id, state.visible)

        fieldRows[field.id]?.visible(state.visible)
        fieldRows[field.id]?.enabled(state.enabled)
        syncFieldComponent(field)

        return previousVisibility != state.visible
    }

    private fun syncFieldComponent(field: GeminioFormField) {
        when (field) {
            is GeminioFormField.StringField -> {
                val expectedText = session.stringValue(field.id).orEmpty()
                stringFields[field.id]?.component?.takeIf { it.text != expectedText }?.text = expectedText
            }

            is GeminioFormField.BooleanField -> {
                val expectedSelected = session.booleanValue(field.id)
                booleanFields[field.id]?.component?.takeIf { it.isSelected != expectedSelected }?.isSelected =
                    expectedSelected
            }

            is GeminioFormField.SuggestField -> {
                val expectedValue = session.suggestValue(field.id)
                val suggestField = suggestFields[field.id]?.component ?: return
                val resolvedValue = suggestField.resolvedValue()
                val expectedText = field.findOption(expectedValue)?.label ?: expectedValue

                if (resolvedValue != expectedValue || suggestField.text != expectedText) {
                    suggestField.setResolvedValue(expectedValue)
                }
            }
        }
    }

    private fun syncAutoManagedBooleanValue(fieldId: String) {
        val field = form.requireField(fieldId) as? GeminioFormField.BooleanField ?: return
        val value = field.initialValueEvaluator?.invoke(session) ?: session.booleanValue(fieldId)
        session.setBooleanValue(fieldId, value)
    }

    private fun validateSuggestField(field: GeminioFormField.SuggestField): ValidationInfo? {
        return when {
            field.isSealed.not() -> null
            suggestFields[field.id]?.component == null -> null
            else -> {
                val component = suggestFields.getValue(field.id).component
                if (component.isValidValue()) {
                    null
                } else {
                    ValidationInfo("'${field.name}' should be one of the suggested values", component)
                }
            }
        }
    }
}
