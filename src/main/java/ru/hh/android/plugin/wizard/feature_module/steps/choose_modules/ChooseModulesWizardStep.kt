package ru.hh.android.plugin.wizard.feature_module.steps.choose_modules

import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.component.module.ModuleRepository
import ru.hh.android.plugin.core.ui.wizard.ChooseItemsStepViewBuilder
import ru.hh.android.plugin.core.ui.wizard.ChooseItemsStepViewTextBundle
import ru.hh.android.plugin.model.MainParametersHolder
import ru.hh.android.plugin.model.enums.PredefinedFeature
import ru.hh.android.plugin.model.extensions.checkFeature
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardModel
import ru.hh.android.plugin.wizard.feature_module.steps.choose_modules.model.ModuleDisplayableItem
import javax.swing.JComponent


class ChooseModulesWizardStep(
        private val model: FeatureModuleWizardModel,
        private val moduleRepository: ModuleRepository
) : WizardStep<FeatureModuleWizardModel>() {

    private val allModulesItems: List<ModuleDisplayableItem>
    private val uiBuilder: ChooseItemsStepViewBuilder<ModuleDisplayableItem>

    private val selectedItems = mutableListOf<ModuleDisplayableItem>()


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
                onModuleSelectionChanged = {
                    // TODO - отобразить README.md
                },
                onModuleItemChecked = { onModuleItemChecked(it) },
                onEnableAllButtonClicked = { enableAllItems() },
                onDisableAllButtonClicked = { disableAllItems() }
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
            this += "analytics"
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

    private fun onModuleItemChecked(item: ModuleDisplayableItem) {
        if (item.isChecked) {
            selectedItems += item
        } else {
            selectedItems.removeIf { it.text == item.text }
        }
    }

    private fun getModulesDisplayableItems(): List<ModuleDisplayableItem> {
        val forceSelectedNames = getForceSelectedModulesNames(model.params)
        val modules = moduleRepository.getLibrariesModules()

        return modules.map { module ->
            val isForceSelected = forceSelectedNames.contains(module.name)

            ModuleDisplayableItem(
                    text = module.name,
                    isForceEnabled = isForceSelected,
                    isChecked = isForceSelected
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