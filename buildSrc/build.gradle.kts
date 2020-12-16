plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20")
    implementation("org.jetbrains.intellij.plugins:gradle-intellij-plugin:0.6.5")
}

gradlePlugin {
    plugins {
        create("setupIdeaPlugin") {
            id = "ru.hh.plugins.gradle.setup_idea_plugin"
            implementationClass = "ru.hh.plugins.gradle.setup_idea_plugin.SetupIdeaPluginGradlePlugin"
            displayName = "Apply common settings to gradle-intellij-plugin"
        }

        create("coreModuleMarker") {
            id = "ru.hh.plugins.gradle.core_module_marker"
            implementationClass = "ru.hh.plugins.gradle.core_module_marker.CoreModuleMarkerPlugin"
            displayName = "Simple marker plugin for core modules that should be involved into updatePlugins.xml"
        }

        create("collectUpdatePluginsXml") {
            id = "ru.hh.plugins.gradle.collect_update_plugins"
            implementationClass = "ru.hh.plugins.gradle.collect_update_plugins.CollectUpdatePluginsXmlPlugin"
            displayName = "Collect updatePlugins.xml file"
        }

        create("buildAllPlugins") {
            id = "ru.hh.plugins.gradle.build_all_plugins"
            implementationClass = "ru.hh.plugins.gradle.build_all_plugins.BuildAllPluginsGradlePlugin"
            displayName = "Build all plugins and move them into single directory"
        }

        create("installGitHooks") {
            id = "ru.hh.plugins.gradle.install_git_hooks"
            implementationClass = "ru.hh.plugins.gradle.install_git_hooks.InstallGitHooksGradlePlugin"
            displayName = "Install git hooks"
        }
    }
}