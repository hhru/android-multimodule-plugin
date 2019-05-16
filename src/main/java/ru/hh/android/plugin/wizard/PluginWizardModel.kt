package ru.hh.android.plugin.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.model.MainParametersHolder
import ru.hh.android.plugin.wizard.step.choose_applications.ChooseApplicationsPresenter
import ru.hh.android.plugin.wizard.step.choose_applications.ChooseApplicationsWizardStep
import ru.hh.android.plugin.wizard.step.choose_applications.model.AppModuleDisplayableItem
import ru.hh.android.plugin.wizard.step.choose_main_parameters.ChooseMainParametersPresenter
import ru.hh.android.plugin.wizard.step.choose_main_parameters.ChooseMainParametersWizardStep
import ru.hh.android.plugin.wizard.step.choose_modules.ChooseModulesPresenter
import ru.hh.android.plugin.wizard.step.choose_modules.ChooseModulesWizardStep
import ru.hh.android.plugin.wizard.step.choose_modules.model.LibraryModuleDisplayableItem


class PluginWizardModel(
        private val project: Project,
        private val createModuleConfig: CreateModuleConfig = CreateModuleConfig(
                mainParams = MainParametersHolder(),
                libraries = emptyList(),
                applications = emptyList()
        )
) : WizardModel("Model for feature module creation wizard.") {

    init {
        initWizardSteps()
    }


    fun getTaskConfig(): CreateModuleConfig {
        return createModuleConfig
    }

    fun setMainParameters(mainParametersHolder: MainParametersHolder) {
        createModuleConfig.mainParams = mainParametersHolder
    }

    fun getMainParameters(): MainParametersHolder? {
        return createModuleConfig.mainParams
    }

    fun setSelectedLibraries(libraries: List<LibraryModuleDisplayableItem>) {
        createModuleConfig.libraries = libraries
    }

    fun setSelectedApplications(applications: List<AppModuleDisplayableItem>) {
        createModuleConfig.applications = applications
    }


    private fun initWizardSteps() {
        listOf(
                createChooseMainParametersStep(),
                createChooseModuleStep(),
                createChooseApplicationsStep()
        ).forEach { add(it) }
    }

    private fun createChooseMainParametersStep(): WizardStep<PluginWizardModel> {
        val presenter = project.getComponent(ChooseMainParametersPresenter::class.java)
        return ChooseMainParametersWizardStep(this, presenter)
    }

    private fun createChooseModuleStep(): WizardStep<PluginWizardModel> {
        val presenter = project.getComponent(ChooseModulesPresenter::class.java)
        return ChooseModulesWizardStep(this, presenter)
    }

    private fun createChooseApplicationsStep(): WizardStep<PluginWizardModel> {
        val presenter = project.getComponent(ChooseApplicationsPresenter::class.java)
        return ChooseApplicationsWizardStep(this, presenter)
    }

}