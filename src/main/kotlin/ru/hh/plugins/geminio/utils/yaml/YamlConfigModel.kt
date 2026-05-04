package ru.hh.plugins.geminio.utils.yaml

interface YamlConfigModel {

    var configFilePath: String

    fun <T : YamlConfigModel> withConfigFilePath(configFilePath: String): T

}
