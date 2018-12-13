package ru.hh.android.plugin.feature_module.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.ChooseApplicationsPresenter
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.ChooseApplicationsWizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters.ChooseMainParametersPresenter
import ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters.ChooseMainParametersWizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.ChooseModulesPresenter
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.ChooseModulesWizardStep


class PluginWizardModel(
        private val project: Project
) : WizardModel("Model for feature module creation wizard.") {

    init {
        initWizardSteps()
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
        return ChooseMainParametersWizardStep(presenter)
    }

    private fun createChooseModuleStep(): WizardStep<PluginWizardModel> {
        val presenter = project.getComponent(ChooseModulesPresenter::class.java)
        return ChooseModulesWizardStep(presenter)
    }

    private fun createChooseApplicationsStep(): WizardStep<PluginWizardModel> {
        val presenter = project.getComponent(ChooseApplicationsPresenter::class.java)
        return ChooseApplicationsWizardStep(presenter)
    }

}