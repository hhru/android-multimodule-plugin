package ru.hh.android.plugin.wizard.step.choose_applications

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.component.module.ModuleInteractor
import ru.hh.android.plugin.core.BasePresenter
import ru.hh.android.plugin.wizard.PluginWizardModel
import ru.hh.android.plugin.wizard.step.choose_applications.model.AppModuleDisplayableItem
import ru.hh.android.plugin.wizard.step.choose_applications.model.converter.AppModuleConverter


class ChooseApplicationsPresenter(
        private val moduleInteractor: ModuleInteractor
) : BasePresenter<PluginWizardModel, ChooseApplicationsView>(), ProjectComponent {

    private var items: List<AppModuleDisplayableItem> = emptyList()


    override fun onCreate(model: PluginWizardModel) {
        super.onCreate(model)

        // TODO - background thread?
        val applicationModules = moduleInteractor.getApplicationModules()
        items = AppModuleConverter().convert(applicationModules)

        view.showList(items)
    }

    override fun onNextButtonClicked(model: PluginWizardModel) {
        super.onNextButtonClicked(model)
        model.setSelectedApplications(items.filter { it.isChecked })
    }


    fun onAppModuleItemSelected(item: AppModuleDisplayableItem) {
        // TODO - показать README в секции описания.
    }

    fun onAppModuleItemToggleChanged(item: AppModuleDisplayableItem) {
        item.isChecked = !item.isChecked
        view.repaintList()
    }

    fun onEnableAllButtonClicked() {
        items.forEach { it.isChecked = true }
        view.repaintList()
    }

    fun onDisableAllButtonClicked() {
        items.forEach { it.isChecked = false }
        view.repaintList()
    }

}