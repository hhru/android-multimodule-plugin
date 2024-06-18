import org.jetbrains.changelog.ChangelogPluginExtension
import org.jetbrains.changelog.date
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.RunIdeTask

plugins {
    id("convention.idea-plugin-base")
    id("org.jetbrains.changelog")
}

fun properties(key: String): String = project.findProperty(key)?.toString().orEmpty()

group = properties("pluginGroup")
version = properties("pluginVersion")

configure<IntelliJPluginExtension> {
    pluginName.set(properties("pluginName"))
}

configure<ChangelogPluginExtension> {
    version.set(properties("pluginVersion"))

    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider { "[$version] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}

tasks.withType<PatchPluginXmlTask> {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    val pluginUntilBuild = properties("pluginUntilBuild")
    if (pluginUntilBuild.isNotBlank()) {
        untilBuild.set(pluginUntilBuild)
    } else {
        println("Skip setup of `untilBuild` property")
    }

    // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
    pluginDescription.set(
        projectDir.resolve("README.md").readText().lines().run {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            if (!containsAll(listOf(start, end))) {
                throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
            }
            subList(indexOf(start) + 1, indexOf(end))
        }.joinToString("\n").run { markdownToHTML(this) }
    )

    // Get the latest available change notes from the changelog file
    changeNotes.set(
        provider {
            changelog.run {
                getOrNull(properties("pluginVersion")) ?: getLatest()
            }.toHTML()
        }
    )
}

tasks.getByName<Zip>("buildPlugin") {
    archiveFileName.set("${properties("pluginName")}.zip")
}

tasks.getByName<RunIdeTask>("runIde") {
    maxHeapSize = "8g"
    minHeapSize = "4g"
}
