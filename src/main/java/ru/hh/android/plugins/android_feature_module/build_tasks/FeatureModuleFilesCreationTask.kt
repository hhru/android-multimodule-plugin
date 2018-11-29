package ru.hh.android.plugins.android_feature_module.build_tasks

import ru.hh.android.plugins.android_feature_module.FreeMarkerConfigurationHolder
import ru.hh.android.plugins.android_feature_module.ProjectInfo
import ru.hh.android.plugins.android_feature_module.WriteActionsFactory
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig
import ru.hh.android.plugins.android_feature_module.models.TemplateData


class FeatureModuleFilesCreationTask
    : BuildTask("Creation of files for feature module") {

    companion object {
        private const val TEMPLATE_NAME_DI_MODULE = "DIModule.kt.ftl"
        private const val TEMPLATE_NAME_API_INTERFACE = "ModuleApi.kt.ftl"
        private const val TEMPLATE_NAME_API_PROVIDER = "ApiProvider.kt.ftl"
        private const val TEMPLATE_NAME_REPOSITORY = "ModuleRepository.kt.ftl"
        private const val TEMPLATE_NAME_INTERACTOR = "ModuleInteractor.kt.ftl"

    }


    override fun internalPerformAction(config: BuildTasksConfig) {
        val basePackageName = "src/main/java/${config.slashedPackageName}"

        val templatesData = mutableListOf(
                TemplateData(
                        relativeFilePath = "$basePackageName/di/${config.formattedLibraryName}Module.kt",
                        templateFileName = TEMPLATE_NAME_DI_MODULE
                )
        ).apply {
            if (config.needCreateAPIInterface) {
                this += TemplateData(
                        relativeFilePath = "$basePackageName/${config.formattedLibraryName}Api.kt",
                        templateFileName = TEMPLATE_NAME_API_INTERFACE
                )
                this += TemplateData(
                        relativeFilePath = "$basePackageName/di/${config.formattedLibraryName}ApiProvider.kt",
                        templateFileName = TEMPLATE_NAME_API_PROVIDER
                )
            }
            if (config.needCreateRepositoryWithInteractor) {
                this += TemplateData(
                        relativeFilePath = "$basePackageName/repository/${config.formattedLibraryName}Repository.kt",
                        templateFileName = TEMPLATE_NAME_REPOSITORY
                )
                this += TemplateData(
                        relativeFilePath = "$basePackageName/interactor/${config.formattedLibraryName}Interactor.kt",
                        templateFileName = TEMPLATE_NAME_INTERACTOR
                )
            }
        }

        WriteActionsFactory.runWriteAction(
                ProjectInfo.getProject(),
                "Create files from templates",
                Runnable {
                    FreeMarkerConfigurationHolder.generateFilesFromTemplates(templatesData, config)
                }
        )
    }
}