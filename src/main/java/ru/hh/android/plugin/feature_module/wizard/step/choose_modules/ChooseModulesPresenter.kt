package ru.hh.android.plugin.feature_module.wizard.step.choose_modules

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.feature_module.component.main_parameters.MainParametersInteractor
import ru.hh.android.plugin.feature_module.component.module.ModuleInteractor
import ru.hh.android.plugin.feature_module.core.BasePresenter
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model.LibraryModuleDisplayableItem
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model.converter.LibraryModuleConverter

class ChooseModulesPresenter(
        private val moduleInteractor: ModuleInteractor,
        private val mainParametersInteractor: MainParametersInteractor
) : BasePresenter<PluginWizardModel, ChooseModulesView>(), ProjectComponent {

    private var items: List<LibraryModuleDisplayableItem> = emptyList()


    override fun onCreate(model: PluginWizardModel) {
        super.onCreate(model)

        // TODO - background thread?
        val libraries = moduleInteractor.getLibrariesModules()
        val forceEnabledModulesNames = mainParametersInteractor
                .getForceEnabledModulesNamesForParameters(model.getMainParameters())

        items = LibraryModuleConverter().convert(libraries, forceEnabledModulesNames)
        view.showList(items)
    }

    override fun onNextButtonClicked(model: PluginWizardModel) {
        super.onNextButtonClicked(model)
        model.setSelectedLibraries(items.filter { it.isChecked })
    }


    fun onLibraryItemSelected(item: LibraryModuleDisplayableItem) {
        // TODO - показать README в секции описания.
    }

    fun onLibraryItemToggleChanged(item: LibraryModuleDisplayableItem) {
        if (item.isForceEnabled) {
            return
        }
        item.isChecked = !item.isChecked
        view.repaintList()
    }

    fun onEnableAllButtonClicked() {
        items.forEach { it.isChecked = true }
        view.repaintList()
    }

    fun onDisableAllButtonClicked() {
        items.filter { !it.isForceEnabled }.forEach { it.isChecked = false }
        view.repaintList()
    }

}