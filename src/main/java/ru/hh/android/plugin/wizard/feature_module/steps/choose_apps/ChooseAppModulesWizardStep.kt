package ru.hh.android.plugin.wizard.feature_module.steps.choose_apps

import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.component.module.ModuleRepository
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardModel
import ru.hh.android.plugin.core.ui.wizard.ChooseItemsStepViewBuilder
import ru.hh.android.plugin.core.ui.wizard.ChooseItemsStepViewTextBundle
import ru.hh.android.plugin.wizard.feature_module.steps.choose_apps.model.AppModuleDisplayableItem
import javax.swing.JComponent


class ChooseAppModulesWizardStep(
        private val model: FeatureModuleWizardModel,
        private val moduleRepository: ModuleRepository
) : WizardStep<FeatureModuleWizardModel>() {

    private val allModulesItems: List<AppModuleDisplayableItem>
    private val uiBuilder: ChooseItemsStepViewBuilder<AppModuleDisplayableItem>

    private val selectedItems = mutableListOf<AppModuleDisplayableItem>()


    init {
        allModulesItems = getModulesDisplayableItems()
        selectedItems += allModulesItems.filter { it.isChecked }

        uiBuilder = ChooseItemsStepViewBuilder(
                textBundle = ChooseItemsStepViewTextBundle(
                        descriptionMessage = "Choose applications which should include new feature module",
                        filterTextFieldMessage = "You can filter applications by names",
                        listDescriptionMessage = "Choose applications"
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

    override fun onFinish(): Boolean {
        model.selectedApps = selectedItems.toList()
        return super.onFinish()
    }


    private fun filterItems(query: String) {
        val filteredList = if (query.isNotBlank()) {
            allModulesItems.filter { it.text.startsWith(query) }
        } else {
            allModulesItems
        }
        uiBuilder.showItems(filteredList)
    }

    private fun onModuleItemChecked(item: AppModuleDisplayableItem) {
        if (item.isChecked) {
            selectedItems += item
        } else {
            selectedItems.removeIf { it.text == item.text }
        }
    }

    private fun getModulesDisplayableItems(): List<AppModuleDisplayableItem> {
        val modules = moduleRepository.getApplicationModules()

        return modules.map { module ->
            AppModuleDisplayableItem(
                    text = module.name,
                    isForceEnabled = false,
                    isChecked = false,
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