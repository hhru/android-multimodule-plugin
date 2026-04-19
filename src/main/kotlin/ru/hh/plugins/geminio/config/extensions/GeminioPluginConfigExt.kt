package ru.hh.plugins.geminio.config.extensions

import ru.hh.plugins.geminio.config.GeminioPluginConfig
import ru.hh.plugins.geminio.config.editor.GeminioSettingsFormState

internal fun GeminioPluginConfig.copyFromFormState(
    state: GeminioSettingsFormState
): GeminioPluginConfig {
    return this.copy(
        configFilePath = state.configFilePath.get(),
        templatesRootDirPath = state.templatesRootDirPath.get(),
        modulesTemplatesRootDirPath = state.modulesTemplatesRootDirPath.get(),
        groupsNames = this.groupsNames.copy(
            forNewGroup = state.nameForNewGroup.get(),
            forNewModulesGroup = state.nameForNewModulesGroup.get(),
        ),
        isDebugEnabled = state.isDebugEnabled.get()
    )
}

internal fun GeminioPluginConfig.isNotFullyInitialized(): Boolean {
    return configFilePath.isBlank() ||
        templatesRootDirPath.isBlank() ||
        modulesTemplatesRootDirPath.isBlank() ||
        groupsNames.isEmpty()
}

private fun GeminioPluginConfig.GroupsNames.isEmpty(): Boolean {
    return forNewGroup.isBlank() || forNewModulesGroup.isBlank()
}
