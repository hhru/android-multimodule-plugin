package ru.hh.android.plugin.feature_module.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.feature_module.model.CreateModuleTaskConfig
import ru.hh.android.plugin.feature_module.model.MainParametersHolder
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.ChooseApplicationsPresenter
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.ChooseApplicationsWizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.model.AppModuleDisplayableItem
import ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters.ChooseMainParametersPresenter
import ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters.ChooseMainParametersWizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.ChooseModulesPresenter
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.ChooseModulesWizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model.LibraryModuleDisplayableItem


class PluginWizardModel(
        private val project: Project,
        private val createModuleTaskConfig: CreateModuleTaskConfig
) : WizardModel("Model for feature module creation wizard.") {

    init {
        initWizardSteps()
    }


    fun getTaskConfig(): CreateModuleTaskConfig {
        return createModuleTaskConfig
    }

    fun setMainParameters(mainParametersHolder: MainParametersHolder) {
        createModuleTaskConfig.mainParametersHolder = mainParametersHolder
    }

    fun getMainParameters(): MainParametersHolder? {
        return createModuleTaskConfig.mainParametersHolder
    }

    fun setSelectedLibraries(libraries: List<LibraryModuleDisplayableItem>) {
        createModuleTaskConfig.libraries = libraries
    }

    fun setSelectedApplications(applications: List<AppModuleDisplayableItem>) {
        createModuleTaskConfig.applications = applications
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