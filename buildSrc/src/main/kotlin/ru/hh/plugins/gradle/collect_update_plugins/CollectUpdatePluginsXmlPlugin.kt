package ru.hh.plugins.gradle.collect_update_plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.register
import org.jetbrains.intellij.IntelliJPlugin
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import ru.hh.plugins.gradle.Constants
import ru.hh.plugins.gradle.core_module_marker.CoreModuleMarkerPlugin
import ru.hh.plugins.gradle.extensions.isRoot


open class CollectUpdatePluginsXmlPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        check(target.isRoot()) {
            "Plugin must be applied to the root project but was applied to ${target.path}"
        }

        target.tasks.register<CollectUpdatePluginsXmlTask>("collectUpdatePluginsXmlTask") {
            val patchPluginXmlTasks = target.getPatchXmlFileTasks()

            // Inputs
            inputFiles.setFrom(patchPluginXmlTasks)

            // Outputs
            outputFile.set(
                target.layout.buildDirectory.file("${Constants.OUTPUT_DIRECTORY_NAME}/updatePlugins.xml")
            )
        }
    }


    private fun Project.getPatchXmlFileTasks(): MutableList<PatchPluginXmlTask> {
        return allprojects
            .filter { project ->
                project.plugins.hasPlugin(IntelliJPlugin::class)
                        && project.plugins.hasPlugin(CoreModuleMarkerPlugin::class).not()
            }
            .mapTo(mutableListOf()) { it.tasks.getByName("patchPluginXml", PatchPluginXmlTask::class) }
    }

}