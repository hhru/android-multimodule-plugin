package ru.hh.plugins.geminio.config

import ru.hh.plugins.extensions.EMPTY


data class GeminioPluginConfig(
    var configFilePath: String = String.EMPTY,
    var templatesRootDirPath: String = String.EMPTY,
    var modulesTemplatesRootDirPath: String = String.EMPTY,
    var groupsNames: GroupsNames = GroupsNames(),
) {

    data class GroupsNames(
        var forNewGroup: String = String.EMPTY,
    )

}