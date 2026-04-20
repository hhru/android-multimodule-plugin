import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.date
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/version_catalogs.html
dependencies {
    implementation("com.vladsch.flexmark:flexmark-all:0.50.42")
    implementation("org.freemarker:freemarker:2.3.30")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.3.1")


    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        // Android Studio Panda 3 Patch 1
        // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html
        androidStudio("2025.3.3.7")

        bundledPlugins(
            "org.jetbrains.android",
            "org.intellij.groovy",
            "com.intellij.java",
            "org.jetbrains.kotlin",
        )

        testFramework(TestFrameworkType.Platform)
    }
}

// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    pluginConfiguration {
        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = version.map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
    header = provider { "[$version] - ${date()}" }
    versionPrefix = ""
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "[Unreleased]"
    groups = listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security")
}

tasks {
    publishPlugin {
        dependsOn(patchChangelog)
    }

    runIde {
        // Android Studio 2025.3.x expects nio-fs classes to be visible to the
        // application classloader when launched via plain `java`.
        classpath += project.files(provider {
            platformPath.resolve("lib/nio-fs.jar").toFile()
        })

        jvmArgumentProviders += CommandLineArgumentProvider {
            listOf("-Didea.kotlin.plugin.use.k2=true")
        }
        maxHeapSize = "8g"
        minHeapSize = "4g"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    failFast = false

    testLogging {
        events("passed", "skipped", "failed")
    }
}

// region Static analysis

detekt {
    source.setFrom("src/main/kotlin")
    config.setFrom("tools/static-analysis/detekt/detekt-config.yaml")
    baseline = file("tools/static-analysis/detekt/detekt-baseline.xml")
}

tasks.named<Detekt>("detekt").configure {
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(false)
    }
}

val detektFormat by tasks.registering(Detekt::class) {
    parallel = true
    autoCorrect = true
    disableDefaultRuleSets = true
    buildUponDefaultConfig = true

    config.setFrom("tools/static-analysis/detekt/detekt-config.yaml")
    baseline.set(file("tools/static-analysis/detekt/detekt-baseline.xml"))

    include("**/*.kt")
    include("**/*.kts")
}

val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."

    source(files("src/main/kotlin"))
    config.setFrom("tools/static-analysis/detekt/detekt-config.yaml")
    baseline.set(file("tools/static-analysis/detekt/detekt-baseline.xml"))

    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)

    include("**/*.kt")
    include("**/*.kts")
}

// endregion
