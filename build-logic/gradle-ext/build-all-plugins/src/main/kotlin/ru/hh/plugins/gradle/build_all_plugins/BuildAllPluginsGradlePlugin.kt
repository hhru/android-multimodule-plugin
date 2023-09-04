package ru.hh.plugins.gradle.build_all_plugins

import Convention_ideaPluginPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.register
import ru.hh.plugins.core_utils.Constants
import ru.hh.plugins.core_utils.isRoot

class BuildAllPluginsGradlePlugin : Plugin<Project> {

    private companion object {
        const val BUILD_PLUGIN_TASK_NAME = "buildPlugin"
    }

    override fun apply(target: Project) {
        check(target.isRoot()) {
            "Plugin must be applied to the root project but was applied to ${target.path}"
        }

        target.tasks.register<BuildAllPluginsTask>("buildAllPlugins") {
            group = "build"
            val buildPluginTasks = target.getBuildPluginTasks()

            // Inputs
            inputFiles.setFrom(buildPluginTasks)

            // Outputs
            outputFiles.set(target.layout.buildDirectory.dir(Constants.OUTPUT_DIRECTORY_NAME))
        }
    }

    private fun Project.getBuildPluginTasks(): MutableList<Zip> {
        return allprojects
            .filter { it.plugins.hasPlugin(Convention_ideaPluginPlugin::class) }
            .mapTo(mutableListOf()) { it.tasks.getByName(BUILD_PLUGIN_TASK_NAME, Zip::class) }
    }
}
