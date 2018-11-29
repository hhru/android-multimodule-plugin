package ru.hh.android.plugins.android_feature_module.build_tasks

import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig
import kotlin.system.measureTimeMillis


abstract class BuildTask(
        private val description: String
) {

    protected abstract fun internalPerformAction(config: BuildTasksConfig)


    fun performAction(config: BuildTasksConfig) {
        println("========================================")
        println("Step: '${javaClass.simpleName}'")
        println("Description: $description")
        val time = measureTimeMillis {
            internalPerformAction(config)
        }
        println("Step '${javaClass.simpleName}' complete [time: $time ms].")
    }

}