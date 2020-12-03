package ru.hh.plugins.geminio.model.yaml

import ru.hh.plugins.model.BuildGradleDependency
import ru.hh.plugins.model.BuildGradleDependencyConfiguration


/**
 * Parser from YAML to [ru.hh.plugins.model.BuildGradleDependency]
 */
@Suppress("UNCHECKED_CAST")
class BuildGradleDependencyParser {

    fun Map<String, String>.toBuildGradleDependency(): BuildGradleDependency {
        check(this.size == 1) {
            "Illegal configuration for adding build.gradle dependency"
        }

        for (configuration in BuildGradleDependencyConfiguration.values()) {
            val value = this[configuration.yamlKey]
            if (value != null) {
                return parseDependency(value, configuration)
            }
        }

        throw IllegalArgumentException("Unknown configuration type for build.gradle dependency [key: ${this.keys}, acceptable values: ${BuildGradleDependencyConfiguration.availableYamlKeys()}]")
    }


    private fun parseDependency(
        value: String,
        configuration: BuildGradleDependencyConfiguration
    ): BuildGradleDependency {
        return when {
            value.isProject() -> BuildGradleDependency.Project(value, configuration)
            value.isMavenArtifact() -> BuildGradleDependency.MavenArtifact(value, configuration)
            else -> BuildGradleDependency.LibsConstant(value, configuration)
        }
    }

    private fun String.isProject(): Boolean {
        return this.startsWith(":") && this.split(":").filter { it.isNotBlank() }.size == 1
    }

    private fun String.isMavenArtifact(): Boolean {
        return this.split(":").size == 3 // domain:artifact:version
    }

}