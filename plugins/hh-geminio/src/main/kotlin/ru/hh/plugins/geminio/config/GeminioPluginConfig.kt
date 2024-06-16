package ru.hh.plugins.geminio.config

import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.utils.yaml.YamlConfigModel

data class GeminioPluginConfig(
    override var configFilePath: String = String.EMPTY,
    var templatesRootDirPath: String = String.EMPTY,
    var modulesTemplatesRootDirPath: String = String.EMPTY,
    var groupsNames: GroupsNames = GroupsNames(),
    var isDebugEnabled: Boolean = false,
) : YamlConfigModel {

    @Suppress("UNCHECKED_CAST")
    override fun <T : YamlConfigModel> setConfigFilePath(configFilePath: String): T {
        return this.copy(configFilePath = configFilePath) as T
    }

    data class GroupsNames(
        var forNewGroup: String = String.EMPTY,
        var forNewModulesGroup: String = String.EMPTY
    )

}
