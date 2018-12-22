package ru.hh.android.plugin.feature_module.component.templates_factory

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.extensions.getRootModulePath
import ru.hh.android.plugin.feature_module.model.TemplatesFactoryModel
import java.io.FileWriter


class TemplatesFactory(
        private val project: Project,
        private val logger: PluginLogger
) : ProjectComponent {

    companion object {
        private const val TEMPLATES_DIR_PATH = "/templates"
    }


    private val config by lazy {
        Configuration(Configuration.VERSION_2_3_28).apply {
            setClassForTemplateLoading(TemplatesFactory::class.java, TEMPLATES_DIR_PATH)

            defaultEncoding = Charsets.UTF_8.name()
            templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            logTemplateExceptions = false
            wrapUncheckedExceptions = true
        }
    }


    fun generate(templatesFactoryModel: TemplatesFactoryModel) {
        val rootModulePath = project.getRootModulePath()
        val templatesFilesList = templatesFactoryModel.getTemplatesFilesList()

        for (fileData in templatesFilesList) {
            val outputFilePath = "$rootModulePath${fileData.targetFilePath}"
            val template = getTemplate(fileData.templateFileName)

            FileWriter(outputFilePath, false).use { writer ->
                logger.log("\tGenerate new file [output path: $outputFilePath, " +
                        "template name: ${fileData.templateFileName}].")

                template.process(templatesFactoryModel.toFreeMarkerDataModel(), writer)
            }
        }
    }


    private fun getTemplate(templateFileName: String): Template {
        return config.getTemplate(templateFileName)
    }


}