package ru.hh.android.plugin.generator.templates

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import ru.hh.android.plugin.config.PluginConfig
import java.io.File
import java.io.StringWriter


class ModuleFilesFactory(private val project: Project) {

    companion object {
        private const val TEMPLATES_DIR_PATH = "/templates"
    }

    private val pluginConfig by lazy {
        PluginConfig.getInstance(project)
    }

    private val psiFileFactory by lazy {
        PsiFileFactory.getInstance(project)
    }

    private val freeMarkerConfig by lazy {
        Configuration(Configuration.VERSION_2_3_28).apply {
            val templatesDir = File("${pluginConfig.pathToPluginFolder}/$TEMPLATES_DIR_PATH")
            setDirectoryForTemplateLoading(templatesDir)

            defaultEncoding = Charsets.UTF_8.name()
            templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            logTemplateExceptions = false
            wrapUncheckedExceptions = true
        }
    }


    fun createFromTemplate(templateData: FileTemplateData, templateProperties: Map<String, Any>): PsiFile {
        val template = freeMarkerConfig.getTemplate(templateData.templateFileName)

        val text = StringWriter().use { writer ->
            template.process(templateProperties, writer)
            writer.buffer.toString()
        }

        return psiFileFactory.createFileFromText(templateData.outputFileName, templateData.outputFileType, text)
    }

}