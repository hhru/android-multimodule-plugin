package ru.hh.android.plugin.generator

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.component.NotificationsFactory
import ru.hh.android.plugin.generator.steps.*
import ru.hh.android.plugin.generator.templates.ModuleFilesFactory
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.model.converter.CreateModuleConfigConverter


class FeatureModuleGenerator(
        private val project: Project,
        private val notificationsFactory: NotificationsFactory,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) : ProjectComponent {

    companion object {
        private const val SUCCESS_MESSAGE = "Success"
    }


    fun create(config: CreateModuleConfig) {
        // This step should be first, because of indexing.
        if (config.mainParams.enableMoxy) {
            val addFeatureModuleIntoMoxyReflectorStubStep = MoxyReflectorStubStep(project)
            addFeatureModuleIntoMoxyReflectorStubStep.execute(config)
        }

        val featureModuleDirsStructureStep = FeatureModuleDirsStructureStep(project)
        val dirsMap = featureModuleDirsStructureStep.execute(config)

        val moduleFilesFactory = ModuleFilesFactory(project)
        val generateFeatureModuleFilesStep = GenerateFeatureModuleFilesStep(createModuleConfigConverter, moduleFilesFactory)
        generateFeatureModuleFilesStep.execute(config, dirsMap)

        val changeSettingsGradleStep = ChangeSettingsGradleStep()
        changeSettingsGradleStep.execute(project, config)

        val addToothpickAnnotationProcessorOptionStep = AddToothpickAnnotationProcessorOptionStep()
        addToothpickAnnotationProcessorOptionStep.execute(config)

        val addFeatureModuleIntoDependenciesStep = AddFeatureModuleIntoDependenciesStep()
        addFeatureModuleIntoDependenciesStep.execute(config)

        notificationsFactory.info(SUCCESS_MESSAGE)
    }

}
