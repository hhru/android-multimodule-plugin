package ru.hh.plugins.garcon.config.extensions

import ru.hh.plugins.garcon.config.GarconPluginConfig
import ru.hh.plugins.garcon.config.editor.GarconSettingsFormState

fun GarconPluginConfig.isNotFullyInitialized(): Boolean {
    return configFilePath.isBlank() ||
        templatesPaths.isEmpty() ||
        widgetsClassesMap.isEmpty()
}

fun GarconPluginConfig.TemplatesPaths.isEmpty(): Boolean {
    return screenPageObjectTemplatePath.isBlank() ||
        rvItemPageObjectTemplatePath.isBlank()
}

internal fun GarconPluginConfig.copyFromFormState(state: GarconSettingsFormState): GarconPluginConfig {
    return this.copy(
        configFilePath = state.configFilePath.get(),
        isDebugEnabled = state.enableDebugMode.get(),
        templatesPaths = this.templatesPaths.copy(
            screenPageObjectTemplatePath = state.screenPageObjectTemplatePath.get(),
            rvItemPageObjectTemplatePath = state.rvItemPageObjectTemplatePath.get(),
        )
    )
}
