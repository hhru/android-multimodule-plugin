import org.gradle.kotlin.dsl.configure
import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.BuildSearchableOptionsTask
import org.jetbrains.intellij.tasks.InstrumentCodeTask
import ru.hh.plugins.ExternalLibrariesExtension

plugins {
    id("convention.kotlin-jvm")
    id("org.jetbrains.intellij")
}

configure<IntelliJPluginExtension> {
    type.set("AI")

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

// Hack for removing errors "call to AnalyticsSettings before initialization"
// https://issuetracker.google.com/issues/224810684?pli=1
tasks.getByName<BuildSearchableOptionsTask>("buildSearchableOptions") {
    enabled = false
}
