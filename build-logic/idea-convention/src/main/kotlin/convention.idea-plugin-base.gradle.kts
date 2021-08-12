import org.jetbrains.intellij.IntelliJPluginExtension

plugins {
    id("convention.kotlin-jvm")
    id("org.jetbrains.intellij")
}

configure<IntelliJPluginExtension> {
    type.set("IC")

    val currentVersion = Libs.chosenIdeaVersion
    if (currentVersion.isLocal) {
        localPath.set(currentVersion.ideVersion)
    } else {
        version.set(currentVersion.ideVersion)
    }
    plugins.set(currentVersion.pluginsNames)
}