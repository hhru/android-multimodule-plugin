package ru.hh.plugins.geminio.actions.module_template.steps

import com.android.tools.idea.npw.model.RenderTemplateModel
import com.android.tools.idea.wizard.model.ModelWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel
import ru.hh.plugins.extensions.layout.boldLabel
import ru.hh.plugins.extensions.layout.onTextChange
import ru.hh.plugins.extensions.openapi.getAndroidApplicationsModules
import ru.hh.plugins.extensions.openapi.getLibrariesModules
import ru.hh.plugins.geminio.services.MarkdownParserService
import ru.hh.plugins.views.CheckBoxListView
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JTextField


/**
 * Wizard step selecting modules
 */
class ChooseModulesModelWizardStep(
    renderTemplateModel: RenderTemplateModel,
    stepTitle: String,
    private val project: Project,
    private val isForAppModules: Boolean,
) : ModelWizardStep<RenderTemplateModel>(renderTemplateModel, stepTitle) {

    private lateinit var filterModulesJTextField: JTextField
    private lateinit var modulesJList: CheckBoxListView<ModuleDisplayableItem>
    private lateinit var readmeBlockTextArea: JEditorPane


    private val allModulesItems: List<ModuleDisplayableItem> by lazy {
        val modules = if (isForAppModules) {
            project.getAndroidApplicationsModules()
        } else {
            project.getLibrariesModules()
        }

        modules.map { module ->
            ModuleDisplayableItem(
                text = module.name,
                isForceEnabled = false,
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
        val markdownService = MarkdownParserService.getInstance(project)
        val parsedMarkdown = markdownService.parseReadmeFile(item.gradleModule)

        changeReadmeText(parsedMarkdown)
    }

    private fun onModuleItemChecked(item: ModuleDisplayableItem) {
        if (item.isChecked) {
            selectedModulesItems += item
        } else {
            selectedModulesItems.removeIf { it.text == item.text }
        }
    }

}