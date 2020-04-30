package ru.hh.android.plugin.generator

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.generator.steps.*
import ru.hh.android.plugin.generator.templates.ModuleFilesFactory
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.model.converter.CreateModuleConfigConverter
import ru.hh.android.plugin.model.enums.PredefinedFeature
import ru.hh.android.plugin.model.extensions.checkFeature
import ru.hh.android.plugin.services.NotificationsFactory


class FeatureModuleGenerator(
        private val project: Project,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) : ProjectComponent {

    companion object {
        private const val SUCCESS_MESSAGE = "Success"
    }


    fun create(config: CreateModuleConfig) {
        val featureModuleDirsStructureStep = FeatureModuleDirsStructureStep(project)
        val dirsMap = featureModuleDirsStructureStep.execute(config)

        val moduleFilesFactory = ModuleFilesFactory(project)
        val generateFeatureModuleFilesStep = GenerateFeatureModuleFilesStep(createModuleConfigConverter, moduleFilesFactory)
        generateFeatureModuleFilesStep.execute(config, dirsMap)

        val changeSettingsGradleStep = ChangeSettingsGradleStep()
        changeSettingsGradleStep.execute(project, config)

        if (config.params.checkFeature(PredefinedFeature.USE_TOOTHPICK_3_SUPPORT).not()) {
            val addToothpickAnnotationProcessorOptionStep = AddToothpickAnnotationProcessorOptionStep()
            addToothpickAnnotationProcessorOptionStep.execute(config)
        }

        val addFeatureModuleIntoDependenciesStep = AddFeatureModuleIntoDependenciesStep()
        addFeatureModuleIntoDependenciesStep.execute(config)

        project.service<NotificationsFactory>().info(SUCCESS_MESSAGE)
    }

}
