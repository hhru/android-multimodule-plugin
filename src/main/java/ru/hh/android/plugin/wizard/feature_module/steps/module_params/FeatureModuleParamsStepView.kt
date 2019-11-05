package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import ru.hh.android.plugin.core.wizard.WizardStepView


interface FeatureModuleParamsStepView : WizardStepView {

    fun setCurrentPackageName(packageName: String)

    fun changeEditPackageNameButtonText(editPackageNameButtonText: String)

    fun enablePackageNameTextField(enable: Boolean)

    fun showPackageNameTextFieldError(show: Boolean)

    fun enableAllPredefinedSettings(enable: Boolean)

}