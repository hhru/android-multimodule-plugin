package ru.hh.plugins.geminio.model.yaml

import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_DEPENDENCIES
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_DEPENDENCY_LIBS_CONSTANT
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_DEPENDENCY_MAVEN_ARTIFACT
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_DEPENDENCY_NAME
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_DEPENDENCY_NOTATION
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_DEPENDENCY_PROJECT
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_DEPENDENCY_TYPE
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_DEPENDENCY_VALUE
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_RECIPE_ADD_DEPENDENCIES
import ru.hh.plugins.model.BuildGradleDependency
import ru.hh.plugins.model.BuildGradleDependencyConfiguration


/**
 * Parser from YAML to [ru.hh.plugins.model.BuildGradleDependency]
 */
@Suppress("UNCHECKED_CAST")
class BuildGradleDependencyParser {

    fun Map<String, Any>.toBuildGradleDependency(configurationForAll: BuildGradleDependencyConfiguration): BuildGradleDependency {
        val mavenArtifactAsString = this[KEY_DEPENDENCY_MAVEN_ARTIFACT] as? String
        val mavenArtifactAsMap = this[KEY_DEPENDENCY_MAVEN_ARTIFACT] as? Map<String, Any>
        val projectAsString = this[KEY_DEPENDENCY_PROJECT] as? String
        val projectAsMap = this[KEY_DEPENDENCY_PROJECT] as? Map<String, Any>
        val libsConstantAsString = this[KEY_DEPENDENCY_LIBS_CONSTANT] as? String
        val libsConstantAsMap = this[KEY_DEPENDENCY_LIBS_CONSTANT] as? Map<String, Any>

        return when {
            mavenArtifactAsString != null -> mavenArtifactAsString.toMavenArtifactDependency(configurationForAll)
            mavenArtifactAsMap != null -> mavenArtifactAsMap.toMavenArtifactDependency(configurationForAll)
            projectAsString != null -> projectAsString.toProjectDependency(configurationForAll)
            projectAsMap != null -> projectAsMap.toProjectDependency(configurationForAll)
            libsConstantAsString != null -> libsConstantAsString.toLibsConstantDependency(configurationForAll)
            libsConstantAsMap != null -> libsConstantAsMap.toLibsConstantDependency(configurationForAll)
            else -> throw IllegalArgumentException("Unknown dependency type in '${KEY_RECIPE_ADD_DEPENDENCIES}' command, inside '${KEY_COMMAND_DEPENDENCIES}' block")
        }
    }


    private fun String.toMavenArtifactDependency(
        configurationForAll: BuildGradleDependencyConfiguration
    ): BuildGradleDependency.MavenArtifact {
        return BuildGradleDependency.MavenArtifact(this, configurationForAll)
    }

    private fun String.toProjectDependency(
        configurationForAll: BuildGradleDependencyConfiguration
    ): BuildGradleDependency.Project {
        return BuildGradleDependency.Project(this, configurationForAll)
    }

    private fun String.toLibsConstantDependency(
        configurationForAll: BuildGradleDependencyConfiguration
    ): BuildGradleDependency.LibsConstant {
        return BuildGradleDependency.LibsConstant(this, configurationForAll)
    }

    private fun Map<String, Any>.toMavenArtifactDependency(
        configurationForAll: BuildGradleDependencyConfiguration
    ): BuildGradleDependency {
        val notation = requireNotNull(this[KEY_DEPENDENCY_NOTATION] as? String) {
            "Not found '${KEY_DEPENDENCY_NOTATION}' for '${KEY_DEPENDENCY_MAVEN_ARTIFACT}' dependency"
        }

        return BuildGradleDependency.MavenArtifact(
            configuration = parseBuildGradleDependencyType(KEY_DEPENDENCY_MAVEN_ARTIFACT, configurationForAll),
            notation = notation
        )
    }

    private fun Map<String, Any>.toProjectDependency(
        configurationForAll: BuildGradleDependencyConfiguration
    ): BuildGradleDependency.Project {
        val name = requireNotNull(this[KEY_DEPENDENCY_NAME] as? String) {
            "Not found '${KEY_DEPENDENCY_NAME}' for '${KEY_DEPENDENCY_PROJECT}' dependency"
        }

        return BuildGradleDependency.Project(
            configuration = parseBuildGradleDependencyType(KEY_DEPENDENCY_PROJECT, configurationForAll),
            projectName = name
        )
    }

    private fun Map<String, Any>.toLibsConstantDependency(
        configurationForAll: BuildGradleDependencyConfiguration
    ): BuildGradleDependency.LibsConstant {
        val constant = requireNotNull(this[KEY_DEPENDENCY_VALUE] as? String) {
            "Not found '${KEY_DEPENDENCY_VALUE}' for '${KEY_DEPENDENCY_LIBS_CONSTANT}' dependency"
        }

        return BuildGradleDependency.LibsConstant(
            configuration = parseBuildGradleDependencyType(KEY_DEPENDENCY_LIBS_CONSTANT, configurationForAll),
            constant = constant
        )
    }


    private fun Map<String, Any>.parseBuildGradleDependencyType(
        dependencyItemName: String,
        configurationForAll: BuildGradleDependencyConfiguration
    ): BuildGradleDependencyConfiguration {
        val typeYamlKey = this[KEY_DEPENDENCY_TYPE] as? String

        return typeYamlKey?.let { yamlKey ->
            requireNotNull(BuildGradleDependencyConfiguration.fromYamlKey(yamlKey)) {
                "Unknown yaml key for BuildGradleDependencyType in '${dependencyItemName}' dependency [unknown key: $yamlKey, available keys: ${BuildGradleDependencyConfiguration.availableYamlKeys()}]"
            }
        } ?: configurationForAll
    }

}