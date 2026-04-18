import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    fun systemProperty(name: String): Provider<String> {
        return providers.systemProperty(name)
    }

    val gradleIntellijPluginVersion = systemProperty("gradleIntellijPluginVersion")
    val gradleChangelogPluginVersion = systemProperty("gradleChangelogPluginVersion")
    val kotlinVersion = systemProperty("kotlinVersion")
    val detektVersion = systemProperty("detektVersion")

    resolutionStrategy {
        eachPlugin {
            val pluginId = requested.id.id

            when {
                pluginId.startsWith("org.jetbrains.intellij.") ->
                    useVersion(gradleIntellijPluginVersion.get())

                pluginId == "org.jetbrains.changelog" ->
                    useVersion(gradleChangelogPluginVersion.get())

                pluginId == "io.gitlab.arturbosch.detekt" ->
                    useVersion(detektVersion.get())

                pluginId.startsWith("org.jetbrains.kotlin.") ->
                    useVersion(kotlinVersion.get())
            }
        }
    }
}

plugins {
    id("org.jetbrains.intellij.platform.settings")
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

    repositories {
        mavenCentral()
        maven("https://packages.atlassian.com/maven/repository/public") {
            mavenContent {
                includeGroupAndSubgroups("""com.atlassian""")
            }
        }
        intellijPlatform {
            defaultRepositories()
        }
    }
}

rootProject.name = "hh-android-plugins"

includeBuild("libraries")
includeBuild("build-logic")

// Plugins
include(":plugins:hh-geminio")
