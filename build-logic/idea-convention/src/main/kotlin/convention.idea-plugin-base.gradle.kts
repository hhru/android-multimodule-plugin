import org.jetbrains.intellij.IntelliJPluginExtension

plugins {
    id("convention.kotlin-jvm")
    id("org.jetbrains.intellij")
}

configure<IntelliJPluginExtension> {
    type = "IC"

    val currentVersion = Libs.chosenIdeaVersion
    if (currentVersion.isLocal) {
        localPath = currentVersion.ideVersion
    } else {
        version = currentVersion.ideVersion
    }
    setPlugins(*currentVersion.pluginsNames.toTypedArray())
}