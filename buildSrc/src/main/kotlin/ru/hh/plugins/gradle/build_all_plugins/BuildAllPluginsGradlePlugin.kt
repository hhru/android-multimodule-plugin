package ru.hh.plugins.gradle.build_all_plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.register
import org.jetbrains.intellij.IntelliJPlugin
import ru.hh.plugins.gradle.Constants
import ru.hh.plugins.gradle.core_module_marker.CoreModuleMarkerPlugin
import ru.hh.plugins.gradle.extensions.isRoot


class BuildAllPluginsGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        check(target.isRoot()) {
            "Plugin must be applied to the root project but was applied to ${target.path}"
        }

        target.tasks.register<BuildAllPluginsTask>("buildAllPlugins") {
            val buildPluginTasks = target.getBuildPluginTasks()

            // Inputs
            inputFiles.setFrom(buildPluginTasks)

            // Outputs
            outputFiles.set(target.layout.buildDirectory.dir(Constants.OUTPUT_DIRECTORY_NAME))
        }
    }


    private fun Project.getBuildPluginTasks(): MutableList<Zip> {
        return allprojects
            .filter { project ->
                project.plugins.hasPlugin(IntelliJPlugin::class)
                        && project.plugins.hasPlugin(CoreModuleMarkerPlugin::class).not()
            }
            .mapTo(mutableListOf()) { it.tasks.getByName("buildPlugin", Zip::class) }
    }

}