import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.date
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask

plugins {
    id("convention.idea-plugin-base")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

private fun properties(key: String): String = project.findProperty(key)?.toString().orEmpty()

project.version = properties("pluginVersion")

intellijPlatform {
    // Hack for removing errors "call to AnalyticsSettings before initialization"
    // https://issuetracker.google.com/issues/224810684?pli=1
    buildSearchableOptions = false

    pluginConfiguration {
        version = project.version.toString()
        description = providers.of(PluginDescriptionValueSource::class.java) {
            parameters.readmeFilePath = project.file("README.md")
        }
        changeNotes = provider {
            changelog.renderItem(
                changelog.getLatest().withHeader(false).withEmptySections(false),
                Changelog.OutputType.HTML
            )
        }
        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            properties("pluginUntilBuild").let {
                if (it.isNotBlank()) {
                    untilBuild = it
                } else {
                    // keep default untilBuild
                }
            }
        }
        vendor {
            name = "hh.ru"
            email = "p.strelchenko@hh.ru"
            url = "https://hh.ru"
        }
    }
}

changelog {
    path = "${project.projectDir}/CHANGELOG.md"
    header = provider { "[$version] - ${date()}" }
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "[Unreleased]"
    groups = listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security")
}

tasks.named<Zip>("buildPlugin").configure {
    archiveFileName = intellijPlatform.projectName.map { "$it.zip" }
}

tasks.named<RunIdeTask>("runIde").configure {
    maxHeapSize = "8g"
    minHeapSize = "4g"
}
