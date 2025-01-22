import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import ru.hh.plugins.ExternalLibrariesExtension
import java.nio.file.Path

plugins {
    id("convention.kotlin-jvm")
    id("org.jetbrains.intellij.platform.base")
}

val currentVersion: ExternalLibrariesExtension.Product = Libs.chosenIdeaVersion

dependencies {
    intellijPlatform {
        when (currentVersion) {
            is ExternalLibrariesExtension.Product.ICBasedIde -> {
                androidStudio(currentVersion.ideVersion, useInstaller = true)

                // XXX: Find a better way to add the bundled version of the plugin to the dependencies
                intellijPlatform.plugin(Libs.androidStudioPlugin)
            }
            is ExternalLibrariesExtension.Product.LocalIde -> {
                local(currentVersion.pathToIde)

                // XXX: Find a better way to add the bundled version of the plugin to the dependencies
                localPlugin(Path.of(currentVersion.pathToIde, "plugins/android").toString())
            }
            else -> error("Should not be reached")
        }

        bundledPlugins(
            "com.intellij.java",
            "org.jetbrains.kotlin",
        )
        pluginVerifier()
        testFramework(TestFrameworkType.Platform)

        // Workaround for https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-faq.html#junit5-test-framework-refers-to-junit4
        testRuntimeOnly(ExternalLibrariesExtension.UnitTests.junit4)
    }
}
