package ru.hh.plugins.garcon.services

import com.android.tools.idea.templates.FreemarkerConfiguration
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.plugins.garcon.config.editor.GarconPluginSettings
import java.io.File
import java.io.StringWriter


@Service
class FreeMarkerWrapper(
    private val project: Project
) {

    companion object {
        fun getInstance(project: Project): FreeMarkerWrapper = project.service()
    }


    private val freeMarkerConfig: FreemarkerConfiguration by lazy {
        println("CREATED FREE_MARKER")
        FreemarkerConfiguration().apply {
            val config = GarconPluginSettings.getConfig(project)
            setDirectoryForTemplateLoading(File(config.configFilePath).parentFile)
        }
    }


    fun resolveTemplate(templateName: String, params: Map<String, Any>): String {
        val template = try {
            freeMarkerConfig.getTemplate(templateName)
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

}