import org.gradle.kotlin.dsl.configure
import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.InstrumentCodeTask
import ru.hh.plugins.ExternalLibrariesExtension

plugins {
    id("convention.kotlin-jvm")
    id("org.jetbrains.intellij")
}

configure<IntelliJPluginExtension> {
    type.set("IC")

    val currentVersion = Libs.chosenIdeaVersion
    when (currentVersion) {
        is ExternalLibrariesExtension.Product.ICBasedIde -> {
            version.set(currentVersion.ideVersion)
        }

        is ExternalLibrariesExtension.Product.LocalIde -> {
            localPath.set(currentVersion.pathToIde)
        }
    }
    plugins.set(currentVersion.pluginsNames)
}

tasks.getByName<InstrumentCodeTask>("instrumentCode") {
    val currentVersion = Libs.chosenIdeaVersion
    if (currentVersion is ExternalLibrariesExtension.Product.LocalIde) {
        compilerVersion.set(currentVersion.compilerVersion)
    }
}
