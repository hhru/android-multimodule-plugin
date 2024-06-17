package ru.hh.plugins.geminio.actions.module_template.steps

import com.android.tools.idea.npw.model.RenderTemplateModel
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.panel
import ru.hh.plugins.extensions.SPACE
import ru.hh.plugins.extensions.UNDERSCORE
import ru.hh.plugins.extensions.layout.onTextChange
import ru.hh.plugins.extensions.openapi.getAndroidApplicationsModules
import ru.hh.plugins.views.CheckBoxListView
import javax.swing.JComponent

/**
 * Wizard step selecting modules
 */
class ChooseModulesModelWizardStep(
    renderTemplateModel: RenderTemplateModel,
    stepTitle: String,
    private val project: Project,
) : ModelWizardStep<RenderTemplateModel>(renderTemplateModel, stepTitle) {

    private lateinit var modulesJList: CheckBoxListView<ModuleDisplayableItem>

    private val allModulesItems: List<ModuleDisplayableItem> by lazy {
        val projectNamePrefix = "${project.name.replace(Char.SPACE, Char.UNDERSCORE)}."
        project.getAndroidApplicationsModules()
            .map { module ->
                ModuleDisplayableItem(
                    text = module.name.removePrefix(projectNamePrefix),
                    isChecked = false,
                    gradleModule = module
                )
            }
    }

    private val selectedModulesItems = mutableListOf<ModuleDisplayableItem>()

    override fun getComponent(): JComponent {
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
                            filterItems(this.text)
                        }
                    }
            }

            row {
                modulesJList = CheckBoxListView(
                    onItemToggleChangedListener = { onModuleItemChecked(it) }
                )
                scrollCell(modulesJList)
                    .label("Choose app-modules:", LabelPosition.TOP)
                    .align(Align.FILL)
            }.resizableRow()

            row {
                button("Enable All") { enableAllItems() }
                button("Disable All") { disableAllItems() }
            }
        }.also {
            updateListView(allModulesItems)
        }
    }

    fun getSelectedModules(): List<Module> {
        return selectedModulesItems.map { it.gradleModule }
    }

    private fun updateListView(items: List<ModuleDisplayableItem>) {
        modulesJList.setItems(items)
    }

    private fun filterItems(query: String) {
        val filteredList = if (query.isNotBlank()) {
            allModulesItems.filter { it.text.contains(query) }
        } else {
            allModulesItems
        }
        updateListView(filteredList)
    }

    private fun onModuleItemChecked(item: ModuleDisplayableItem) {
        if (item.isChecked) {
            selectedModulesItems += item
        } else {
            selectedModulesItems.removeIf { it.text == item.text }
        }
    }

    private fun enableAllItems() {
        selectedModulesItems.clear()
        selectedModulesItems += allModulesItems

        val newItems = allModulesItems.map { module ->
            module.copy(isChecked = true)
        }
        updateListView(newItems)
    }

    private fun disableAllItems() {
        selectedModulesItems.clear()
        selectedModulesItems += allModulesItems.filter { it.isForceEnabled }

        val newItems = allModulesItems.map { module ->
            if (module.isForceEnabled) {
                module.copy()
            } else {
                module.copy(isChecked = false)
            }
        }
        updateListView(newItems)
    }
}
