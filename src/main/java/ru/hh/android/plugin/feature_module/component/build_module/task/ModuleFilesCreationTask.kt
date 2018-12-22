package ru.hh.android.plugin.feature_module.component.build_module.task

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.component.templates_factory.TemplatesFactory
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.converter.CreateModuleConfigConverter

class ModuleFilesCreationTask(
        project: Project,
        logger: PluginLogger,
        private val templatesFactory: TemplatesFactory,
        private val createModuleConfigConverter: CreateModuleConfigConverter
) : BuildModuleTask("Module files creation", project, logger) {

    override fun execute(config: CreateModuleConfig) {
        templatesFactory.generate(createModuleConfigConverter.convert(config))
    }

}