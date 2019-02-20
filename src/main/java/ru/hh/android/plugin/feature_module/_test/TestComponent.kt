package ru.hh.android.plugin.feature_module._test

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.feature_module._test.steps.*
import ru.hh.android.plugin.feature_module._test.templates.NewTemplateFactory
import ru.hh.android.plugin.feature_module.component.NotificationsFactory
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.converter.CreateModuleConfigConverter


class TestComponent(
        private val project: Project,
        private val newTemplateFactory: NewTemplateFactory,
        private val notificationsFactory: NotificationsFactory,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) : ProjectComponent {

    fun create(config: CreateModuleConfig) {
        val featureModuleDirsStructureStep = FeatureModuleDirsStructureStep()
        val dirsMap = featureModuleDirsStructureStep.createDirsStructure(project, config)

        val generateFeatureModuleFilesStep = GenerateFeatureModuleFilesStep(newTemplateFactory, createModuleConfigConverter)
        generateFeatureModuleFilesStep.generate(config, dirsMap)

        val changeSettingsGradleStep = ChangeSettingsGradleStep()
        changeSettingsGradleStep.change(project, config)

        val addToothpickAnnotationProcessorOptionStep = AddToothpickAnnotationProcessorOptionStep()
        addToothpickAnnotationProcessorOptionStep.execute(config)

        val addFeatureModuleIntoDependenciesStep = AddFeatureModuleIntoDependenciesStep()
        addFeatureModuleIntoDependenciesStep.execute(config)

        notificationsFactory.info("Success")
    }

}
