package ru.hh.android.plugin.model

import ru.hh.android.plugin.extensions.replaceWordsBreakers
import ru.hh.android.plugin.model.enums.PredefinedFeature
import ru.hh.android.plugin.model.extensions.checkFeature
import ru.hh.android.plugin.wizard.feature_module.steps.choose_apps.model.AppModuleDisplayableItem
import ru.hh.android.plugin.wizard.feature_module.steps.choose_modules.model.ModuleDisplayableItem


class CreateModuleConfig(
        val mainParams: MainParametersHolder,
        val libraries: List<ModuleDisplayableItem>,
        val applications: List<AppModuleDisplayableItem>
) {

    val formattedLibraryName: String
        get() {
            return with(StringBuilder()) {
                mainParams.moduleName
                        .replaceWordsBreakers()
                        .split(' ')
                        .map { it.capitalize() }
                        .forEach { append(it) }
                toString()
            }
        }

    val layoutName: String
        get() {
            return mainParams.moduleName
                    .replaceWordsBreakers()
                    .split(' ')
                    .joinToString(separator = "_") { it.toLowerCase() }
        }

    fun getPredefinedSettingsMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        PredefinedFeature.values().forEach { value ->
            map[value.freeMarkerParamToken] = checkFeature(value)
        }

        return map
    }

    fun checkFeature(feature: PredefinedFeature) = mainParams.checkFeature(feature)

}