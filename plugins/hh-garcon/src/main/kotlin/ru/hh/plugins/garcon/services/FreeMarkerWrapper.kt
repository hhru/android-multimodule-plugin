package ru.hh.plugins.garcon.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.plugins.freemarker_wrapper.FreemarkerConfiguration
import ru.hh.plugins.garcon.config.editor.GarconPluginSettings
import java.io.File

@Service(Service.Level.PROJECT)
class FreeMarkerWrapper(
    private val project: Project
) {

    companion object {
        fun getInstance(project: Project): FreeMarkerWrapper = project.service()
    }

    private var freeMarkerConfig: FreemarkerConfiguration? = null
    private var lastConfigFilePath: String? = null

    fun resolveTemplate(templateName: String, params: Map<String, Any>): String {
        return getFreeMarkerConfig().resolveTemplate(templateName, params)
    }

    private fun getFreeMarkerConfig(): FreemarkerConfiguration {
        val config = GarconPluginSettings.getConfig(project)

        if (freeMarkerConfig == null || lastConfigFilePath != config.configFilePath) {
            val configDir = File(config.configFilePath).parent
            freeMarkerConfig = recreateFreemarkerConfiguration(configDir)
            lastConfigFilePath = config.configFilePath
        }

        return requireNotNull(freeMarkerConfig)
    }

    private fun recreateFreemarkerConfiguration(configFilePath: String): FreemarkerConfiguration {
        return FreemarkerConfiguration(configFilePath)
    }
}
