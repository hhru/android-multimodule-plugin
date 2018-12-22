package ru.hh.android.plugin.feature_module.component.build_module.task

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.extensions.runWriteAction
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import kotlin.system.measureTimeMillis


abstract class BuildModuleTask(
        private val name: String,
        protected val project: Project,
        protected val logger: PluginLogger
) {

    protected abstract fun execute(config: CreateModuleConfig)


    fun performBuildAction(config: CreateModuleConfig) {
        logger.log("Start task: $name")
        val time = measureTimeMillis {
            project.runWriteAction {
                execute(config)
            }
        }
        logger.log("End task: $name [time: $time ms]")
    }

}