package ru.hh.plugins.geminio.model.yaml

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import ru.hh.plugins.geminio.sdk.model.recipe.BuildGradleDependency
import ru.hh.plugins.geminio.sdk.model.enums.BuildGradleDependencyConfiguration
import ru.hh.plugins.geminio.sdk.parsers.BuildGradleDependencyParser


class BuildGradleDependencyParserSpec : FreeSpec({

    val parser = BuildGradleDependencyParser()

    fun Map<String, String>.getDependency(): BuildGradleDependency {
        val given = this
        return with(parser) { given.toBuildGradleDependency() }
    }


    "Correct parsing of configuration types" {
        for (configuration in BuildGradleDependencyConfiguration.values()) {
            val given = mapOf(configuration.yamlKey to "Libs.item")
            val dependency = given.getDependency()

            dependency.configuration shouldBe configuration
        }
    }

    "Should throw exception if unknown configuration type" {
        val given = mapOf("unknown" to "Libs.item")

        val ex = shouldThrow<IllegalArgumentException> { given.getDependency() }

        ex.message shouldStartWith "Unknown configuration type for build.gradle dependency [key: [unknown]"
    }

    "Should parse as maven artifact" {
        listOf(
            "org.jetbrains:artifact:1.0",
            "org.jetbrains:artifact:\${ext.version}",
            "org.jetbrains:artifact:\$someVersion"
        ).forEach { value ->
            val given = mapOf("kapt" to value)

            val dependency = given.getDependency()

            dependency should { it is BuildGradleDependency.MavenArtifact }
            dependency.value shouldBe value
        }
    }

    "Should parse as library constant" {
        listOf(
            "Libs",
            "Libs.jetpack",
            "Libs.jetpack.compose",
            "library",
            "ext.libs.compose"
        ).forEach { value ->
            val given = mapOf("kapt" to value)

            val dependency = given.getDependency()

            dependency should { it is BuildGradleDependency.LibsConstant }
            dependency.value shouldBe value
        }
    }

    "Should parse as project dependency" {
        listOf(
            ":shared",
            ":shared-core-model",
        ).forEach { value ->
            val given = mapOf("kapt" to value)

            val dependency = given.getDependency()

            dependency should { it is BuildGradleDependency.Project }
            dependency.value shouldBe value
        }
    }

})