package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import ru.hh.android.plugin.core.wizard.WizardStepView


interface FeatureModuleParamsStepView : WizardStepView {

    fun enableAllPredefinedSettings(enable: Boolean)

}