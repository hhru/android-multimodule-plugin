package ru.hh.android.plugin.feature_module.component.build_module.factory

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.feature_module.component.build_module.task.*
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.component.templates_factory.TemplatesFactory
import ru.hh.android.plugin.feature_module.model.converter.CreateModuleConfigConverter
import ru.hh.android.plugin.feature_module.model.enums.CreateModuleBuildTaskType
import ru.hh.android.plugin.feature_module.model.enums.CreateModuleBuildTaskType.*


class BuildModuleTaskFactory(
        private val project: Project,
        private val logger: PluginLogger,
        private val templatesFactory: TemplatesFactory,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) : ProjectComponent {

    fun createTask(type: CreateModuleBuildTaskType): BuildModuleTask {
        return when (type) {
            DIRS_STRUCTURE_CREATION -> DirsStructureCreationTask(project, logger)
            MODULE_FILES_CREATION -> ModuleFilesCreationTask(project, logger, templatesFactory, createModuleConfigConverter)
            SETTINGS_GRADLE_FILE_MODIFICATION -> SettingsGradleFileModificationTask(project, logger)
            BUILD_GRADLE_DEPENDENCIES_BLOCK_MODIFICATION -> BuildGradleDependenciesBlockModificationTask(project, logger)
            TOOTHPICK_ANNOTATION_PROCESSING_CONFIG_MODIFICATION -> ToothpickAnnotationProcessingConfigModificationTask(project, logger)
            MOXY_REFLECTOR_STUB_MODIFICATION -> MoxyReflectorStubModificationTask(project, logger)
        }
    }

}