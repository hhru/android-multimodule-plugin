package ru.hh.android.plugin.feature_module.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.ChooseApplicationsController
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.ChooseApplicationsWizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters.ChooseMainParametersController
import ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters.ChooseMainParametersWizardStep
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.ChooseModulesController
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
        val controller = project.getComponent(ChooseMainParametersController::class.java)
        return ChooseMainParametersWizardStep(controller)
    }

    private fun createChooseModuleStep(): WizardStep<PluginWizardModel> {
        val controller = project.getComponent(ChooseModulesController::class.java)
        return ChooseModulesWizardStep(controller)
    }

    private fun createChooseApplicationsStep(): WizardStep<PluginWizardModel> {
        val controller = project.getComponent(ChooseApplicationsController::class.java)
        return ChooseApplicationsWizardStep(controller)
    }

}