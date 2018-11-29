package ru.hh.android.plugins.android_feature_module.build_tasks

import ru.hh.android.plugins.android_feature_module.FreeMarkerConfigurationHolder
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig
import ru.hh.android.plugins.android_feature_module.models.TemplateData


class AndroidModuleBaseFileCreationTask
    : BuildTask("Creation of base files for each Android module") {

    companion object {
        private val BASE_FILES_TEMPLATES_DATA_LIST = listOf(
                TemplateData(
                        relativeFilePath = "src/main/AndroidManifest.xml",
                        templateFileName = "AndroidManifest.xml.ftl"
                ),
                TemplateData(
                        relativeFilePath = "build.gradle",
                        templateFileName = "build.gradle.ftl"
                ),
                TemplateData(
                        relativeFilePath = ".gitignore",
                        templateFileName = "gitignore.ftl"
                ),
                TemplateData(
                        relativeFilePath = "proguard-rules.pro",
                        templateFileName = "proguard-rules.pro.ftl"
                )
        )
    }


    override fun internalPerformAction(config: BuildTasksConfig) {
        FreeMarkerConfigurationHolder.generateFilesFromTemplates(BASE_FILES_TEMPLATES_DATA_LIST, config)
    }

}