package ru.hh.plugins.garcon.config.extensions

import ru.hh.plugins.garcon.config.GarconPluginConfig


fun GarconPluginConfig.isNotFullyInitialized(): Boolean {
    return configFilePath.isBlank()
            || templatesPaths.isEmpty()
            || widgetsClassesMap.isEmpty()
}

fun GarconPluginConfig.TemplatesPaths.isEmpty(): Boolean {
    return screenPageObjectTemplatePath.isBlank()
            || rvItemPageObjectTemplatePath.isBlank()
}