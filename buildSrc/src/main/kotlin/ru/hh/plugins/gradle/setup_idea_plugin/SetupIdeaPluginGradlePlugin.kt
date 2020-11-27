package ru.hh.plugins.gradle.setup_idea_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.intellij.IntelliJPlugin
import org.jetbrains.intellij.IntelliJPluginExtension


/**
 * Plugin for applying common settings to gradle-intellij-plugin
 */
class SetupIdeaPluginGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        check(target.plugins.hasPlugin(IntelliJPlugin::class.java)) {
            "Plugin should be applied to project with gradle-intellij-plugin, but was applied to ${target.path}"
        }

        with(target.extensions.getByType(IntelliJPluginExtension::class.java)) {
            type = "IC"

            val currentVersion = Versions.chosenProduct
            if (currentVersion.isLocal) {
                localPath = currentVersion.ideVersion
            } else {
                version = currentVersion.ideVersion
            }
            setPlugins(*currentVersion.pluginsNames.toTypedArray())
        }
    }

}