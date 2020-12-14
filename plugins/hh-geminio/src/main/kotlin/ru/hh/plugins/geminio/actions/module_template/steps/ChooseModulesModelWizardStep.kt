package ru.hh.plugins.geminio.actions.module_template.steps

import com.android.tools.idea.npw.model.RenderTemplateModel
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel
import ru.hh.plugins.extensions.SPACE
import ru.hh.plugins.extensions.UNDERSCORE
import ru.hh.plugins.extensions.layout.boldLabel
import ru.hh.plugins.extensions.layout.onTextChange
import ru.hh.plugins.extensions.openapi.getAndroidApplicationsModules
import ru.hh.plugins.extensions.openapi.getLibrariesModules
import ru.hh.plugins.geminio.services.MarkdownParserService
import ru.hh.plugins.views.CheckBoxListView
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JTextField
import javax.swing.border.EmptyBorder


/**
 * Wizard step selecting modules
 */
class ChooseModulesModelWizardStep(
    renderTemplateModel: RenderTemplateModel,
    stepTitle: String,
    private val project: Project,
    private val isForAppModules: Boolean,
) : ModelWizardStep<RenderTemplateModel>(renderTemplateModel, stepTitle) {

    companion object {
        private const val TEXT_AREA_PADDING = 10
    }


    private lateinit var filterModulesJTextField: JTextField
    private lateinit var modulesJList: CheckBoxListView<ModuleDisplayableItem>
    private lateinit var readmeBlockTextArea: JEditorPane

    private val isReadmeAvailable: Boolean get() = isForAppModules.not()


    private val allModulesItems: List<ModuleDisplayableItem> by lazy {
        val modules = if (isForAppModules) {
            project.getAndroidApplicationsModules()
        } else {
            project.getLibrariesModules()
        }

        val projectNamePrefix = "${project.name.replace(Char.SPACE, Char.UNDERSCORE)}."
        modules.map { module ->
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
            row { boldLabel("Choose modules as dependencies for new feature module") }

            titledRow("You can filter modules by names") {
                row {
                    filterModulesJTextField = JTextField().apply {
                        onTextChange { filterItems(filterModulesJTextField.text) }
                    }
                    filterModulesJTextField()
                }
            }

            titledRow("Choose modules") {
                row {
                    modulesJList = CheckBoxListView(
                        onItemSelectedListener = { changeReadmeBlockText(it) },
                        onItemToggleChangedListener = { onModuleItemChecked(it) }
                    )

                    scrollPane(modulesJList)
                }
            }

            if (isReadmeAvailable) {
                titledRow("Readme of selected module") {
                    row {
                        readmeBlockTextArea = JEditorPane().apply {
                            contentType = "text/html"
                            border =
                                EmptyBorder(TEXT_AREA_PADDING, TEXT_AREA_PADDING, TEXT_AREA_PADDING, TEXT_AREA_PADDING)
                        }
                        scrollPane(readmeBlockTextArea)
                    }
                }
            }

            row {
                cell {
                    button("Enable all") { enableAllItems() }
                    button("Disable all") { disableAllItems() }
                }
            }
        }.also {
            updateListView(allModulesItems)
        }
    }


    private fun updateListView(items: List<ModuleDisplayableItem>) {
        modulesJList.setItems(items)
    }

    private fun changeReadmeText(text: String) {
        readmeBlockTextArea.text = text
    }

    private fun filterItems(query: String) {
        val filteredList = if (query.isNotBlank()) {
            allModulesItems.filter { it.text.startsWith(query) }
        } else {
            allModulesItems
        }
        updateListView(filteredList)
    }

    private fun changeReadmeBlockText(item: ModuleDisplayableItem) {
        if (isReadmeAvailable) {
            val markdownService = MarkdownParserService.getInstance(project)
            val parsedMarkdown = markdownService.parseReadmeFile(item.gradleModule)

            changeReadmeText(parsedMarkdown)
        }
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