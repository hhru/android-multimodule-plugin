package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.core.wizard.BaseWizardStep
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardModel


class FeatureModuleParamsWizardStep(
        project: Project,
        model: FeatureModuleWizardModel
) : FeatureModuleParamsStepView, BaseWizardStep<
        FeatureModuleWizardModel,
        FeatureModuleParamsStepView,
        FeatureModuleParamsFormState,
        FeatureModuleParamsStepViewBuilder,
        FeatureModuleParamsPresenter>(model, project) {

    override fun getViewBuilder(): FeatureModuleParamsStepViewBuilder {
        return FeatureModuleParamsStepViewBuilder(
                project = project,
                defaultModuleType = FeatureModuleType.CUSTOM_PATH,
                onEnableAllPredefinedSettingsButtonClicked = {
                    presenter.onEnableAllPredefinedSettingsButtonClicked()
                },
                onDisableAllPredefinedSettingsButtonClicked = {
                    presenter.onDisableAllPredefinedSettingsButtonClicked()
                }
        )
    }

    override fun getPresenter(project: Project): FeatureModuleParamsPresenter {
        return FeatureModuleParamsPresenter()
    }

    override fun enableAllPredefinedSettings(enable: Boolean) {
        uiBuilder.enableAllPredefinedSettings(enable)
    }

}