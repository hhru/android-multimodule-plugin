package ru.hh.plugins

object PluginsConstants {
    const val BUILD_GRADLE_DEPENDENCIES_BLOCK_NAME = "dependencies"
    const val BUILD_GRADLE_PLUGINS_BLOCK_NAME = "plugins"

    const val YAML_FILE_EXTENSION = ".yaml"
    val YAML_FILES_FILTER_EXTENSION = YAML_FILE_EXTENSION.removePrefix(".")
}
