import org.jetbrains.changelog.ChangelogPluginExtension
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.date
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.PatchPluginXmlTask

plugins {
    id("convention.idea-plugin-base")
    id("org.jetbrains.changelog")
}

fun properties(key: String) = project.findProperty(key).toString()


group = properties("pluginGroup")
version = properties("pluginVersion")

configure<IntelliJPluginExtension> {
    pluginName.set(properties("pluginName"))
}

configure<ChangelogPluginExtension> {
    version = properties("pluginVersion")

    path = "${project.projectDir}/CHANGELOG.md"
    header = closure { "[$version] - ${date()}" }
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "[Unreleased]"
    groups = listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security")
}

tasks.withType<PatchPluginXmlTask> {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))

    // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
    pluginDescription.set(
        closure {
            File(projectDir, "README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        }.call()
    )

    // Get the latest available change notes from the changelog file
    changeNotes.set(
        closure {
            changelog.getLatest().toHTML()
        }.call()
    )
}

tasks.getByName<Zip>("buildPlugin") {
    archiveFileName.set("${properties("pluginName")}.zip")
}