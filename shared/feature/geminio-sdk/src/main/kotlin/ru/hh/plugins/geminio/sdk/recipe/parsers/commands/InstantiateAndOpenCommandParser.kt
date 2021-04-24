package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression


private const val KEY_COMMAND_FROM = "from"
private const val KEY_COMMAND_TO = "to"


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand.InstantiateAndOpen] command.
 */
internal fun Map<String, Any>.toInstantiateAndOpenCommand(
    sectionName: String
): RecipeCommand.InstantiateAndOpen {
    val fromString = requireNotNull(this[KEY_COMMAND_FROM] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_COMMAND_FROM
        )
    }
    val toString = requireNotNull(this[KEY_COMMAND_TO] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_COMMAND_TO,
        )
    }

    return RecipeCommand.InstantiateAndOpen(
        from = fromString.toRecipeExpression(sectionName),
        to = toString.toRecipeExpression(sectionName),
    )
}