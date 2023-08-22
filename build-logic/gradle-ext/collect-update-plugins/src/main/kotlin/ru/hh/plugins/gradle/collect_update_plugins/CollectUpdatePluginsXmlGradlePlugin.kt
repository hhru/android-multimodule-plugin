package ru.hh.plugins.gradle.collect_update_plugins

import Convention_ideaPluginPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.register
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import ru.hh.plugins.core_utils.Constants
import ru.hh.plugins.core_utils.isRoot

open class CollectUpdatePluginsXmlGradlePlugin : Plugin<Project> {

    private companion object {
        const val PATCH_PLUGIN_XML_TASK_NAME = "patchPluginXml"
    }

    override fun apply(target: Project) {
        check(target.isRoot()) {
            "Plugin must be applied to the root project but was applied to ${target.path}"
        }

        target.tasks.register<CollectUpdatePluginsXmlTask>("collectUpdatePluginsXmlTask") {
            this.group = "build"
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
            .filter { it.plugins.hasPlugin(Convention_ideaPluginPlugin::class) }
            .mapTo(mutableListOf()) { it.tasks.getByName(PATCH_PLUGIN_XML_TASK_NAME, PatchPluginXmlTask::class) }
    }
}
