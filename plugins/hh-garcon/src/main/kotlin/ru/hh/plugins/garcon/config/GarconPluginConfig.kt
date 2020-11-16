package ru.hh.plugins.garcon.config

import ru.hh.plugins.extensions.EMPTY


data class GarconPluginConfig(
    var configFilePath: String = String.EMPTY,
    var enableDebugMode: Boolean = false,
    var templatesPaths: TemplatesPaths = TemplatesPaths(),
    var widgetsClassesMap: MutableMap<String, WidgetDescription> = mutableMapOf()
) {

    data class TemplatesPaths(
        var screenPageObjectTemplatePath: String = String.EMPTY,
        var rvItemPageObjectTemplatePath: String = String.EMPTY,
    )

    data class WidgetDescription(
        var kakaoWidgetFQN: String = String.EMPTY,
        var idSuffixes: MutableList<String> = mutableListOf()
    )

}