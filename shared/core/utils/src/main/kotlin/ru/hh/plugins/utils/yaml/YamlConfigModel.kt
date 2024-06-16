package ru.hh.plugins.utils.yaml

interface YamlConfigModel {

    var configFilePath: String

    fun <T : YamlConfigModel> setConfigFilePath(configFilePath: String): T

}
