package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression

private const val KEY_COMMAND_FILE = "file"

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand.Open] command.
 */
internal fun Map<String, Any>.toOpenCommand(sectionName: String): RecipeCommand.Open {
    val fileString = requireNotNull(this[KEY_COMMAND_FILE] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_COMMAND_FILE
        )
    }

    return RecipeCommand.Open(
        file = fileString.toRecipeExpression(sectionName),
    )
}
