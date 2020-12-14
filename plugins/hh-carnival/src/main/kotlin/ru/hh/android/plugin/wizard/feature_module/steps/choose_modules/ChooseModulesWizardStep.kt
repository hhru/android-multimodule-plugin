package ru.hh.android.plugin.wizard.feature_module.steps.choose_modules

import com.android.tools.idea.util.toIoFile
import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import ru.hh.android.plugin.core.ui.wizard.ChooseItemsStepViewBuilder
import ru.hh.android.plugin.core.ui.wizard.ChooseItemsStepViewTextBundle
import ru.hh.android.plugin.extensions.EMPTY
import ru.hh.android.plugin.extensions.findPsiFileByName
import ru.hh.android.plugin.model.MainParametersHolder
import ru.hh.android.plugin.model.enums.PredefinedFeature
import ru.hh.android.plugin.model.extensions.checkFeature
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardModel
import ru.hh.android.plugin.wizard.feature_module.steps.choose_modules.model.ModuleDisplayableItem
import ru.hh.plugins.extensions.openapi.getLibrariesModules
import javax.swing.JComponent


class ChooseModulesWizardStep(
    private val project: Project,
    private val model: FeatureModuleWizardModel
) : WizardStep<FeatureModuleWizardModel>() {

    companion object {
        private const val README_FILE_NAME = "README.md"
    }

    private val allModulesItems: List<ModuleDisplayableItem>
    private val uiBuilder: ChooseItemsStepViewBuilder<ModuleDisplayableItem>

    private val selectedItems = mutableListOf<ModuleDisplayableItem>()

    private val options: MutableDataSet by lazy {
        MutableDataSet().apply {
            set(Parser.EXTENSIONS, listOf(TablesExtension.create(), StrikethroughExtension.create()))
            set(HtmlRenderer.SOFT_BREAK, "<br />\n")
        }
    }

    private val parser: Parser by lazy {
        Parser.builder(options).build()
    }

    private val htmlRenderer: HtmlRenderer by lazy {
        HtmlRenderer.builder(options).build()
    }


    init {
        allModulesItems = getModulesDisplayableItems()
        selectedItems += allModulesItems.filter { it.isChecked }

        uiBuilder = ChooseItemsStepViewBuilder(
            textBundle = ChooseItemsStepViewTextBundle(
                descriptionMessage = "Choose modules as dependencies for new feature module",
                filterTextFieldMessage = "You can filter modules by names",
                listDescriptionMessage = "Choose modules"
            ),
            onFilterTextChanged = { filterItems(it) },
            onModuleSelectionChanged = { changeReadmeBlockText(it) },
            onModuleItemChecked = { onModuleItemChecked(it) },
            onEnableAllButtonClicked = { enableAllItems() },
            onDisableAllButtonClicked = { disableAllItems() },
            isReadmeBlockAvailable = true
        )
    }

    override fun prepare(state: WizardNavigationState?): JComponent {
        return uiBuilder.build().also {
            uiBuilder.showItems(allModulesItems)
        }
    }

    override fun onNext(model: FeatureModuleWizardModel): WizardStep<*> {
        model.selectedModules = selectedItems.toList()
        return super.onNext(model)
    }


    private fun getForceSelectedModulesNames(params: MainParametersHolder): Set<String> {
        return mutableSetOf<String>().apply {
            this += "logger"
            this += "analytics-api"
            this += "core-utils"

            if (params.checkFeature(PredefinedFeature.ADD_UI_MODULES_DEPENDENCIES)) {
                this += "base-ui"
            }

            if (params.checkFeature(PredefinedFeature.NEED_CREATE_API_INTERFACE)) {
                this += "network-source"
                this += "network-auth-source"
            }
        }
    }


    private fun filterItems(query: String) {
        val filteredList = if (query.isNotBlank()) {
            allModulesItems.filter { it.text.startsWith(query) }
        } else {
            allModulesItems
        }
        uiBuilder.showItems(filteredList)
    }

    private fun changeReadmeBlockText(item: ModuleDisplayableItem) {
        val readmeFile = item.gradleModule.findPsiFileByName(README_FILE_NAME)?.virtualFile?.toIoFile()
        if (readmeFile == null) {
            uiBuilder.changeReadmeText(String.EMPTY)
        } else {
            val document = parser.parseReader(readmeFile.reader())
            val html = htmlRenderer.render(document)
            uiBuilder.changeReadmeText(html)
        }
    }

    private fun onModuleItemChecked(item: ModuleDisplayableItem) {
        if (item.isChecked) {
            selectedItems += item
        } else {
            selectedItems.removeIf { it.text == item.text }
        }
    }

    private fun getModulesDisplayableItems(): List<ModuleDisplayableItem> {
        val forceSelectedNames = getForceSelectedModulesNames(model.params)
        val modules = project.getLibrariesModules()

        return modules.map { module ->
            val isForceSelected = forceSelectedNames.contains(module.name)

            ModuleDisplayableItem(
                text = module.name,
                isForceEnabled = isForceSelected,
                isChecked = isForceSelected,
                gradleModule = module
            )
        }
    }

    private fun enableAllItems() {
        selectedItems.clear()
        selectedItems += allModulesItems

        val newItems = allModulesItems.map { module ->
            module.copy(isChecked = true)
        }
        uiBuilder.showItems(newItems)
    }

    private fun disableAllItems() {
        selectedItems.clear()
        selectedItems += allModulesItems.filter { it.isForceEnabled }

        val newItems = allModulesItems.map { module ->
            if (module.isForceEnabled) {
                module.copy()
            } else {
                module.copy(isChecked = false)
            }
        }
        uiBuilder.showItems(newItems)
    }

}