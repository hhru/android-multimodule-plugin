import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.IntelliJInstrumentCodeTask
import org.gradle.kotlin.dsl.configure

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

@Suppress("UnstableApiUsage")
tasks.getByName<IntelliJInstrumentCodeTask>("instrumentCode") {
    if (Libs.chosenIdeaVersion.isLocal) {
        compilerVersion.set(Libs.chosenIdeaVersion.compilerVersion)
    }
}
