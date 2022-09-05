package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand.AddGradlePlugins] command.
 */
internal fun List<String>.toAddGradlePluginsCommand(sectionName: String): RecipeCommand.AddGradlePlugins {
    check(this.isNotEmpty()) {
        ParsersErrorsFactory.sectionErrorMessage(
            sectionName = sectionName,
            message = """
            |Illegal configuration for adding gradle plugin into plugins section, 
            |section should contain at least single value
            |
            """.trimMargin()
        )
    }

    return RecipeCommand.AddGradlePlugins(
        pluginsIds = this.toList()
    )
}
