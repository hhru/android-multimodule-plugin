package ru.hh.plugins.garcon.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.plugins.garcon.config.editor.GarconPluginSettings
import ru.hh.plugins.utils.freemarker.FreemarkerConfiguration
import java.io.File
import java.io.StringWriter


@Service
class FreeMarkerWrapper(
    private val project: Project
) {

    companion object {
        fun getInstance(project: Project): FreeMarkerWrapper = project.service()
    }

    private var freeMarkerConfig: FreemarkerConfiguration? = null
    private var lastConfigFilePath: String? = null


    fun resolveTemplate(templateName: String, params: Map<String, Any>): String {
        val template = try {
            getFreeMarkerConfig().getTemplate(templateName)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw IllegalArgumentException("Can't find template [templateName: $templateName]")
        }

        return StringWriter().use { writer ->
            try {
                template.process(params, writer)
                writer.buffer.toString()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw UnsupportedOperationException()
            }
        }
    }

    private fun getFreeMarkerConfig(): FreemarkerConfiguration {
        val config = GarconPluginSettings.getConfig(project)

        if (freeMarkerConfig == null || lastConfigFilePath != config.configFilePath) {
            freeMarkerConfig = recreateFreemarkerConfiguration(config.configFilePath)
            lastConfigFilePath = config.configFilePath
        }

        return requireNotNull(freeMarkerConfig)
    }

    private fun recreateFreemarkerConfiguration(configFilePath: String): FreemarkerConfiguration {
        return FreemarkerConfiguration().apply {
            setDirectoryForTemplateLoading(File(configFilePath).parentFile)
        }
    }

}