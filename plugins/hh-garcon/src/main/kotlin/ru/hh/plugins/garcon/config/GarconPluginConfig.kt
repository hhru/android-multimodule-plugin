package ru.hh.plugins.garcon.config

import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.utils.yaml.YamlConfigModel

class GarconPluginConfig(
    override var configFilePath: String = String.EMPTY,
    var isDebugEnabled: Boolean = false,
    var templatesPaths: TemplatesPaths = TemplatesPaths(),
    var widgetsClassesMap: MutableMap<String, WidgetDescription> = mutableMapOf()
) : YamlConfigModel {

    @Suppress("UNCHECKED_CAST")
    override fun <T : YamlConfigModel> withConfigFilePath(configFilePath: String): T {
        return this.copy(configFilePath = configFilePath) as T
    }

    fun copy(
        configFilePath: String = this.configFilePath,
        isDebugEnabled: Boolean = this.isDebugEnabled,
        templatesPaths: TemplatesPaths = this.templatesPaths,
        widgetsClassesMap: MutableMap<String, WidgetDescription> = this.widgetsClassesMap,
    ): GarconPluginConfig {
        return GarconPluginConfig(
            configFilePath = configFilePath,
            isDebugEnabled = isDebugEnabled,
            templatesPaths = templatesPaths,
            widgetsClassesMap = widgetsClassesMap,
        )
    }

    data class TemplatesPaths(
        var screenPageObjectTemplatePath: String = String.EMPTY,
        var rvItemPageObjectTemplatePath: String = String.EMPTY,
    )

    data class WidgetDescription(
        var kakaoWidgetFQN: String = String.EMPTY,
        var idSuffixes: MutableList<String> = mutableListOf()
    )
}
