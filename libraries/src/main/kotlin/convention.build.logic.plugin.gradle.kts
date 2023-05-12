import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import ru.hh.plugins.ExternalLibrariesExtension

plugins {
    kotlin("jvm") apply false
    id("convention.libraries")
}

plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin> {
    kotlin {
        jvmToolchain {
            languageVersion.set(
                JavaLanguageVersion.of(
                    extensions.getByType<ExternalLibrariesExtension>().javaVersion.majorVersion.toInt()
                )
            )
        }
    }
}
