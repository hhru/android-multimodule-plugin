package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.BuildGradleDependency
import ru.hh.plugins.geminio.sdk.recipe.models.commands.BuildGradleDependencyConfiguration
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand.AddDependencies] command.
 */
internal fun List<Map<String, Any>>.toAddDependenciesCommand(sectionName: String): RecipeCommand.AddDependencies {
    val dependencies = this.map { it.toBuildGradleDependency(sectionName) }

    return RecipeCommand.AddDependencies(
        dependencies = dependencies
    )
}


private fun Map<String, Any>.toBuildGradleDependency(sectionName: String): BuildGradleDependency {
    check(this.size == 1) {
        sectionErrorMessage(
            sectionName = sectionName,
            message = "Illegal configuration for adding build.gradle dependency - each list item should be single map entry, e.g. `- kapt: Libs.toothpick`"
        )
    }

    for (configuration in BuildGradleDependencyConfiguration.values()) {
        val value = this[configuration.yamlKey] as? String
        if (value != null) {
            return parseDependency(value, configuration)
        }
    }

    throw IllegalArgumentException(
        sectionUnknownEnumKeyErrorMessage(
            sectionName = sectionName,
            key = keys.first(),
            acceptableValues = BuildGradleDependencyConfiguration.availableYamlKeys()
        )
    )
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
    return this.startsWith(":")
}

private fun String.isMavenArtifact(): Boolean {
    return this.split(":").size == 3 // domain:artifact:version
}