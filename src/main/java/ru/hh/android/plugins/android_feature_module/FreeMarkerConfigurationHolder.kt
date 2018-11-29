package ru.hh.android.plugins.android_feature_module

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig
import ru.hh.android.plugins.android_feature_module.models.TemplateData
import java.io.FileWriter

object FreeMarkerConfigurationHolder {

    private const val FREE_MARKER_TEMPLATES_DIR = "/ru/hh/android/plugins/android_feature_module/templates"

    private const val TEMPLATE_TOKEN_PACKAGE_NAME = "package_name"
    private const val TEMPLATE_TOKEN_FORMATTED_LIBRARY_NAME = "formatted_library_name"
    private const val TEMPLATE_TOKEN_ENABLE_MOXY = "enable_moxy"
    private const val TEMPLATE_TOKEN_ADD_UI_MODULES_DEPENDENCIES = "need_add_ui_modules_dependencies"
    private const val TEMPLATE_TOKEN_NEED_CREATE_API_INTERFACE = "need_create_api_interface"
    private const val TEMPLATE_TOKEN_NEED_CREATE_REPOSITORY_WITH_INTERACTOR = "need_create_repository_with_interactor"
    private const val TEMPLATE_TOKEN_LIBRARIES_MODULES = "libraries_modules"


    private val config by lazy {
        Configuration(Configuration.VERSION_2_3_28).apply {
            setClassForTemplateLoading(FreeMarkerConfigurationHolder.javaClass, FREE_MARKER_TEMPLATES_DIR)

            defaultEncoding = Charsets.UTF_8.name()
            templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            logTemplateExceptions = false
            wrapUncheckedExceptions = true
        }
    }


    fun generateFilesFromTemplates(templatesData: List<TemplateData>, config: BuildTasksConfig) {
        val basePath = "${ProjectInfo.getRootModuleDirPath()}/${config.moduleType.typeRootFolder}"

        val dataModel = generateFreeMarkerDataModel(config)

        for (templateData in templatesData) {
            val outputFileName = "$basePath/${config.moduleName}/${templateData.relativeFilePath}"
            val template = getTemplate(templateData.templateFileName)

            println("\tGenerate new file [output path: $outputFileName, template name: ${templateData.templateFileName}].")

            FileWriter(outputFileName, false).use { writer ->
                template.process(dataModel, writer)
            }
        }
    }


    private fun getTemplate(templateFileName: String): Template {
        return config.getTemplate(templateFileName)
    }

    private fun generateFreeMarkerDataModel(buildTasksConfig: BuildTasksConfig): Map<String, Any> {
        return with(buildTasksConfig) {
            mapOf(
                    TEMPLATE_TOKEN_PACKAGE_NAME to packageName,
                    TEMPLATE_TOKEN_FORMATTED_LIBRARY_NAME to formattedLibraryName,
                    TEMPLATE_TOKEN_ENABLE_MOXY to enableMoxy,
                    TEMPLATE_TOKEN_ADD_UI_MODULES_DEPENDENCIES to addUIModuleDependencies,
                    TEMPLATE_TOKEN_NEED_CREATE_API_INTERFACE to needCreateAPIInterface,
                    TEMPLATE_TOKEN_NEED_CREATE_REPOSITORY_WITH_INTERACTOR to needCreateRepositoryWithInteractor,
                    TEMPLATE_TOKEN_LIBRARIES_MODULES to librariesModules.map { it.text }
            )
        }
    }

}