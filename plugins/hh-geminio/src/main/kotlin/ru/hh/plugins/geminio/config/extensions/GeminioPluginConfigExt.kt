package ru.hh.plugins.geminio.config.extensions

import ru.hh.plugins.geminio.config.GeminioPluginConfig
import ru.hh.plugins.utils.yaml.YamlUtils
import java.io.FileNotFoundException

fun GeminioPluginConfig.isNotFullyInitialized(): Boolean {
    return configFilePath.isBlank() ||
        templatesRootDirPath.isBlank() ||
        modulesTemplatesRootDirPath.isBlank() ||
        groupsNames.isEmpty()
}

private fun GeminioPluginConfig.GroupsNames.isEmpty(): Boolean {
    return forNewGroup.isBlank() || forNewModulesGroup.isBlank()
}

internal fun tryLoadFromConfigFile(configFilePath: String): Result<GeminioPluginConfig> {
    val configFromYaml = YamlUtils.loadFromConfigFile<GeminioPluginConfig>(
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
