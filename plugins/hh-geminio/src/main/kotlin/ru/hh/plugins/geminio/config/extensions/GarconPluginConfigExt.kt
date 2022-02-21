package ru.hh.plugins.geminio.config.extensions

import ru.hh.plugins.geminio.config.GeminioPluginConfig

fun GeminioPluginConfig.isNotFullyInitialized(): Boolean {
    return configFilePath.isBlank() ||
        templatesRootDirPath.isBlank() ||
        modulesTemplatesRootDirPath.isBlank() ||
        groupsNames.isEmpty()
}

private fun GeminioPluginConfig.GroupsNames.isEmpty(): Boolean {
    return forNewGroup.isBlank() || forNewModulesGroup.isBlank()
}
