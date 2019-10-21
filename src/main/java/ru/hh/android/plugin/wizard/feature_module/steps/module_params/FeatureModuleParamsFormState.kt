package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import ru.hh.android.plugin.core.wizard.WizardStepFormState
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.android.plugin.model.enums.PredefinedFeature


data class FeatureModuleParamsFormState(
        val moduleName: String,
        val packageName: String,
        val moduleType: FeatureModuleType,
        val customModuleTypePath: String,
        val enabledFeatures: List<PredefinedFeature>
) : WizardStepFormState