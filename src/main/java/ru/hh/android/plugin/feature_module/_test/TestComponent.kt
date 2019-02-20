package ru.hh.android.plugin.feature_module._test

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.feature_module._test.steps.ChangeSettingsGradleStep
import ru.hh.android.plugin.feature_module._test.steps.FeatureModuleDirsStructureStep
import ru.hh.android.plugin.feature_module._test.steps.GenerateFeatureModuleFilesStep
import ru.hh.android.plugin.feature_module._test.templates.NewTemplateFactory
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.converter.CreateModuleConfigConverter


class TestComponent(
        private val project: Project,
        private val newTemplateFactory: NewTemplateFactory,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) : ProjectComponent {

    fun create(config: CreateModuleConfig) {
        val featureModuleDirsStructureStep = FeatureModuleDirsStructureStep()
        val dirsMap = featureModuleDirsStructureStep.createDirsStructure(project, config)

        val generateFeatureModuleFilesStep = GenerateFeatureModuleFilesStep(newTemplateFactory, createModuleConfigConverter)
        generateFeatureModuleFilesStep.generate(config, dirsMap)

        val changeSettingsGradleStep = ChangeSettingsGradleStep()
        changeSettingsGradleStep.change(project, config)


    }

}
