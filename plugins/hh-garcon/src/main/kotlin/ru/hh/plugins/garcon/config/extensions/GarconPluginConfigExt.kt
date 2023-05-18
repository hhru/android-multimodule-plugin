package ru.hh.plugins.garcon.config.extensions

import ru.hh.plugins.garcon.config.GarconPluginConfig
import ru.hh.plugins.utils.yaml.YamlUtils
import java.io.FileNotFoundException

fun GarconPluginConfig.isNotFullyInitialized(): Boolean {
    return configFilePath.isBlank() ||
        templatesPaths.isEmpty() ||
        widgetsClassesMap.isEmpty()
}

fun GarconPluginConfig.TemplatesPaths.isEmpty(): Boolean {
    return screenPageObjectTemplatePath.isBlank() ||
        rvItemPageObjectTemplatePath.isBlank()
}

internal fun tryLoadFromConfigFile(configFilePath: String): Result<GarconPluginConfig> {
    val configFromYaml = YamlUtils.loadFromConfigFile<GarconPluginConfig>(
        configFilePath = configFilePath,
        onError = {
            return Result.failure(it)
        }
    )?.copy(configFilePath = configFilePath)

    return if (configFromYaml != null) {
        Result.success(configFromYaml)
    } else {
        Result.failure(FileNotFoundException("File `$configFilePath` not found"))
    }
}
