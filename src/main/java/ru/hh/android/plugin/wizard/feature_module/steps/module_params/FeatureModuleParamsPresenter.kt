package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import ru.hh.android.plugin.core.wizard.WizardStepPresenter
import ru.hh.android.plugin.extensions.isCorrectPackageName
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardModel


class FeatureModuleParamsPresenter
    : WizardStepPresenter<FeatureModuleWizardModel, FeatureModuleParamsStepView, FeatureModuleParamsFormState>() {

    override fun validateForm(formState: FeatureModuleParamsFormState): Boolean {
        return with(formState) {
            packageName.isCorrectPackageName() &&
                    (moduleType != FeatureModuleType.CUSTOM_PATH || customModuleTypePath.isNotBlank())
        }
    }

    override fun updateModel(model: FeatureModuleWizardModel, formState: FeatureModuleParamsFormState) {
        model.params = FeatureModuleParamsFormStateConverter().convert(formState)
    }


    fun onEnableAllPredefinedSettingsButtonClicked() {
        view.enableAllPredefinedSettings(true)
    }

    fun onDisableAllPredefinedSettingsButtonClicked() {
        view.enableAllPredefinedSettings(false)
    }

}