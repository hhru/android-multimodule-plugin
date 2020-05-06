package ru.hh.android.plugin.generator.templates

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.config.PluginConfig
import java.io.File
import java.io.StringWriter


class ModuleFilesFactory(private val project: Project) {

    private val pluginConfig by lazy {
        PluginConfig.getInstance(project)
    }

    private val psiFileFactory by lazy {
        PsiFileFactory.getInstance(project)
    }

    private val freeMarkerConfig by lazy {
        Configuration().apply {
            val templatesDir = File("${pluginConfig.pathToPluginFolder}/${PluginConstants.DEFAULT_TEMPLATES_DIR_NAME}")
            setDirectoryForTemplateLoading(templatesDir)

            defaultEncoding = Charsets.UTF_8.name()
            templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        }
    }


    fun createFromTemplate(templateData: FileTemplateData, templateProperties: Map<String, Any>): PsiFile {
        val template = try {
            freeMarkerConfig.getTemplate(templateData.templateFileName)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw IllegalArgumentException("Can't find template [templateData: $templateData]")
        }

        val text = StringWriter().use { writer ->
            try {
                template.process(templateProperties, writer)
                writer.buffer.toString()
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw UnsupportedOperationException()
            }
        }

        return psiFileFactory.createFileFromText(templateData.outputFileName, templateData.outputFileType, text)
    }

}