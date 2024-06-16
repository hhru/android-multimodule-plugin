package ru.hh.plugins.geminio.config

import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.utils.yaml.YamlUtils
import java.io.FileNotFoundException

data class GeminioPluginConfig(
    var configFilePath: String = String.EMPTY,
    var templatesRootDirPath: String = String.EMPTY,
    var modulesTemplatesRootDirPath: String = String.EMPTY,
    var groupsNames: GroupsNames = GroupsNames(),
    var isDebugEnabled: Boolean = false,
) {

    internal companion object {

        fun tryLoadFromConfigFile(configFilePath: String): Result<GeminioPluginConfig> {
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

    }

    data class GroupsNames(
        var forNewGroup: String = String.EMPTY,
        var forNewModulesGroup: String = String.EMPTY
    )
}
