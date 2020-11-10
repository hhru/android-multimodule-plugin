package ru.hh.android.plugins.garcon.generator.freemarker

import com.android.tools.idea.templates.FreemarkerConfiguration
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugins.garcon.TemplatesConstants
import ru.hh.android.plugins.garcon.config.PluginConfig
import java.io.File
import java.io.StringWriter


class FreeMarkerWrapper(
    private val project: Project
) : ProjectComponent {

    private val freeMarkerConfig: FreemarkerConfiguration by lazy {
        FreemarkerConfiguration().apply {
            val templatesFolderPath = PluginConfig.getInstance(project).pluginFolderDirPath +
                    TemplatesConstants.TEMPLATES_FOLDER_NAME

            setDirectoryForTemplateLoading(File(templatesFolderPath))
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