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
import ru.hh.plugins.model.BuildGradleDependencyType


/**
 * Parser from YAML to [ru.hh.plugins.model.BuildGradleDependency]
 */
@Suppress("UNCHECKED_CAST")
class BuildGradleDependencyParser {

    fun Map<String, Any>.toBuildGradleDependency(typeForAll: BuildGradleDependencyType): BuildGradleDependency {
        val mavenArtifactAsString = this[KEY_DEPENDENCY_MAVEN_ARTIFACT] as? String
        val mavenArtifactAsMap = this[KEY_DEPENDENCY_MAVEN_ARTIFACT] as? Map<String, Any>
        val projectAsString = this[KEY_DEPENDENCY_PROJECT] as? String
        val projectAsMap = this[KEY_DEPENDENCY_PROJECT] as? Map<String, Any>
        val libsConstantAsString = this[KEY_DEPENDENCY_LIBS_CONSTANT] as? String
        val libsConstantAsMap = this[KEY_DEPENDENCY_LIBS_CONSTANT] as? Map<String, Any>

        return when {
            mavenArtifactAsString != null -> mavenArtifactAsString.toMavenArtifactDependency(typeForAll)
            mavenArtifactAsMap != null -> mavenArtifactAsMap.toMavenArtifactDependency(typeForAll)
            projectAsString != null -> projectAsString.toProjectDependency(typeForAll)
            projectAsMap != null -> projectAsMap.toProjectDependency(typeForAll)
            libsConstantAsString != null -> libsConstantAsString.toLibsConstantDependency(typeForAll)
            libsConstantAsMap != null -> libsConstantAsMap.toLibsConstantDependency(typeForAll)
            else -> throw IllegalArgumentException("Unknown dependency type in '${KEY_RECIPE_ADD_DEPENDENCIES}' command, inside '${KEY_COMMAND_DEPENDENCIES}' block")
        }
    }


    private fun String.toMavenArtifactDependency(
        typeForAll: BuildGradleDependencyType
    ): BuildGradleDependency.MavenArtifact {
        return BuildGradleDependency.MavenArtifact(this, typeForAll)
    }

    private fun String.toProjectDependency(
        typeForAll: BuildGradleDependencyType
    ): BuildGradleDependency.Project {
        return BuildGradleDependency.Project(this, typeForAll)
    }

    private fun String.toLibsConstantDependency(
        typeForAll: BuildGradleDependencyType
    ): BuildGradleDependency.LibsConstant {
        return BuildGradleDependency.LibsConstant(this, typeForAll)
    }

    private fun Map<String, Any>.toMavenArtifactDependency(
        typeForAll: BuildGradleDependencyType
    ): BuildGradleDependency {
        val notation = requireNotNull(this[KEY_DEPENDENCY_NOTATION] as? String) {
            "Not found '${KEY_DEPENDENCY_NOTATION}' for '${KEY_DEPENDENCY_MAVEN_ARTIFACT}' dependency"
        }

        return BuildGradleDependency.MavenArtifact(
            type = parseBuildGradleDependencyType(KEY_DEPENDENCY_MAVEN_ARTIFACT, typeForAll),
            notation = notation
        )
    }

    private fun Map<String, Any>.toProjectDependency(
        typeForAll: BuildGradleDependencyType
    ): BuildGradleDependency.Project {
        val name = requireNotNull(this[KEY_DEPENDENCY_NAME] as? String) {
            "Not found '${KEY_DEPENDENCY_NAME}' for '${KEY_DEPENDENCY_PROJECT}' dependency"
        }

        return BuildGradleDependency.Project(
            type = parseBuildGradleDependencyType(KEY_DEPENDENCY_PROJECT, typeForAll),
            projectName = name
        )
    }

    private fun Map<String, Any>.toLibsConstantDependency(
        typeForAll: BuildGradleDependencyType
    ): BuildGradleDependency.LibsConstant {
        val constant = requireNotNull(this[KEY_DEPENDENCY_VALUE] as? String) {
            "Not found '${KEY_DEPENDENCY_VALUE}' for '${KEY_DEPENDENCY_LIBS_CONSTANT}' dependency"
        }

        return BuildGradleDependency.LibsConstant(
            type = parseBuildGradleDependencyType(KEY_DEPENDENCY_LIBS_CONSTANT, typeForAll),
            constant = constant
        )
    }


    private fun Map<String, Any>.parseBuildGradleDependencyType(
        dependencyItemName: String,
        typeForAll: BuildGradleDependencyType
    ): BuildGradleDependencyType {
        val typeYamlKey = this[KEY_DEPENDENCY_TYPE] as? String

        return typeYamlKey?.let { yamlKey ->
            requireNotNull(BuildGradleDependencyType.fromYamlKey(yamlKey)) {
                "Unknown yaml key for BuildGradleDependencyType in '${dependencyItemName}' dependency [unknown key: $yamlKey, available keys: ${BuildGradleDependencyType.availableYamlKeys()}]"
            }
        } ?: typeForAll
    }

}