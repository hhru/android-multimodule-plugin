package ru.hh.plugins.geminio.config

import ru.hh.plugins.utils.yaml.YamlConfigModel

class GeminioPluginConfig(
    override var configFilePath: String = "",
    var templatesRootDirPath: String = "",
    var modulesTemplatesRootDirPath: String = "",
    var groupsNames: GroupsNames = GroupsNames(),
    var isDebugEnabled: Boolean = false,
) : YamlConfigModel {

    @Suppress("UNCHECKED_CAST")
    override fun <T : YamlConfigModel> withConfigFilePath(configFilePath: String): T {
        return this.copy(configFilePath = configFilePath) as T
    }

    fun copy(
        configFilePath: String = this.configFilePath,
        templatesRootDirPath: String = this.templatesRootDirPath,
        modulesTemplatesRootDirPath: String = this.modulesTemplatesRootDirPath,
        groupsNames: GroupsNames = this.groupsNames,
        isDebugEnabled: Boolean = this.isDebugEnabled,
    ): GeminioPluginConfig {
        return GeminioPluginConfig(
            configFilePath = configFilePath,
            templatesRootDirPath = templatesRootDirPath,
            modulesTemplatesRootDirPath = modulesTemplatesRootDirPath,
            groupsNames = groupsNames,
            isDebugEnabled = isDebugEnabled,
        )
    }

    data class GroupsNames(
        var forNewGroup: String = "",
        var forNewModulesGroup: String = ""
    )
}
