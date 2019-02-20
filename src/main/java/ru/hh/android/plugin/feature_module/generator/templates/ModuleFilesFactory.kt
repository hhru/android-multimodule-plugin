package ru.hh.android.plugin.feature_module.generator.templates

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.StringWriter


class ModuleFilesFactory(private val project: Project) : ProjectComponent {

    companion object {
        private const val TEMPLATES_DIR_PATH = "/templates"
    }


    private val freeMarkerConfig by lazy {
        Configuration(Configuration.VERSION_2_3_28).apply {
            setClassForTemplateLoading(ModuleFilesFactory::class.java, TEMPLATES_DIR_PATH)

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

        return PsiFileFactory.getInstance(project)
                .createFileFromText(templateData.outputFileName, templateData.outputFileType, text)
    }

}