package ru.hh.plugins.geminio.wizard

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import ru.hh.plugins.geminio.actions.module_template.steps.ModuleDisplayableItem
import ru.hh.plugins.geminio.common.extensions.SPACE
import ru.hh.plugins.geminio.common.extensions.UNDERSCORE
import ru.hh.plugins.geminio.services.android.getAndroidApplicationsModules
import ru.hh.plugins.geminio.ui.checkboxlist.CheckBoxListView
import ru.hh.plugins.geminio.ui.extensions.onTextChange
import java.awt.Dimension
import javax.swing.JComponent

/**
 * Custom replacement for the old module-selection wizard step.
 *
 * It reuses the existing checkbox list view but no longer depends on `ModelWizardStep`.
 */
internal class GeminioChooseModulesDialog(
    private val project: Project,
    title: String,
    confirmActionText: String = "Finish",
    private val preferredDialogSize: Dimension = Dimension(DEFAULT_DIALOG_WIDTH, DEFAULT_DIALOG_HEIGHT),
) : DialogWrapper(project, true) {

    private companion object {
        const val DEFAULT_DIALOG_WIDTH = 680
        const val DEFAULT_DIALOG_HEIGHT = 520
    }

    private val modulesListView = CheckBoxListView<ModuleDisplayableItem>()

    private val allModulesItems: List<ModuleDisplayableItem> by lazy {
        val projectNamePrefix = "${project.name.replace(Char.SPACE, Char.UNDERSCORE)}."
        project.getAndroidApplicationsModules()
            .map { module ->
                ModuleDisplayableItem(
                    text = module.name.removePrefix(projectNamePrefix),
                    isChecked = false,
                    gradleModule = module,
                )
            }
    }

    private var currentFilterQuery: String = ""

    init {
        this.title = title
        isResizable = true
        setOKButtonText(confirmActionText)
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                label("Choose application modules that should add new feature module as dependency")
                    .bold()
            }

            row {
                textField()
                    .resizableColumn()
                    .align(Align.FILL)
                    .label("Filter:", LabelPosition.TOP)
                    .comment("You can filter modules by names")
                    .applyToComponent {
                        onTextChange {
                            currentFilterQuery = text
                            updateListView()
                        }
                    }
            }

            row {
                scrollCell(modulesListView)
                    .label("Choose app-modules:", LabelPosition.TOP)
                    .align(Align.FILL)
            }.resizableRow()

            row {
                button("Enable All") {
                    allModulesItems.forEach { item ->
                        if (item.isForceEnabled.not()) {
                            item.isChecked = true
                        }
                    }
                    updateListView()
                }
                button("Disable All") {
                    allModulesItems.forEach { item ->
                        item.isChecked = item.isForceEnabled
                    }
                    updateListView()
                }
            }
        }.apply {
            preferredSize = Dimension(
                JBUI.scale(preferredDialogSize.width),
                JBUI.scale(preferredDialogSize.height),
            )
            updateListView()
        }
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return modulesListView
    }

    fun getSelectedModules(): List<Module> {
        return allModulesItems
            .filter { it.isChecked }
            .map { it.gradleModule }
    }

    private fun updateListView() {
        val filteredItems = if (currentFilterQuery.isBlank()) {
            allModulesItems
        } else {
            allModulesItems.filter { it.text.contains(currentFilterQuery, ignoreCase = true) }
        }

        modulesListView.setItems(filteredItems)
    }
}
