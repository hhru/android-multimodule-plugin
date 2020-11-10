package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import ru.hh.android.plugin.core.model.ModelConverter
import ru.hh.android.plugin.extensions.replaceMultipleSplashes
import ru.hh.android.plugin.model.MainParametersHolder
import ru.hh.android.plugin.model.enums.FeatureModuleType


class FeatureModuleParamsFormStateConverter : ModelConverter<FeatureModuleParamsFormState, MainParametersHolder> {

    override fun convert(item: FeatureModuleParamsFormState): MainParametersHolder {
        val moduleName = item.moduleName
        val moduleType = item.moduleType

        return MainParametersHolder(
                moduleName = moduleName,
                packageName = item.packageName,
                moduleType = moduleType,
                settingsGradleModulePath = getModulePathForSettingsGradle(item),
                customModuleTypePath = item.customModuleTypePath,
                enabledFeatures = item.enabledFeatures
        )
    }


    private fun getModulePathForSettingsGradle(formState: FeatureModuleParamsFormState): String {
        return when (formState.moduleType) {
            FeatureModuleType.CUSTOM_PATH -> "./${formState.customModuleTypePath}".replaceMultipleSplashes()
            else -> "./${formState.moduleType.folderPrefix}/${formState.moduleName}".replaceMultipleSplashes()
        }
    }

}