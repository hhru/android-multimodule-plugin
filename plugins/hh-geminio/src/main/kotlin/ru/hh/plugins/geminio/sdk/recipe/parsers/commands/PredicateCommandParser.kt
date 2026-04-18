@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression
import ru.hh.plugins.utils.yaml.YamlUtils.getBooleanOrStringExpression

private const val KEY_COMMAND_VALID_IF = "validIf"
private const val KEY_COMMAND_COMMANDS = "commands"
private const val KEY_COMMAND_ELSE_COMMANDS = "elseCommands"

internal fun Map<String, Any>.toPredicateCommand(sectionName: String): RecipeCommand.Predicate {
    val validIfString = requireNotNull(getBooleanOrStringExpression(KEY_COMMAND_VALID_IF)) {
        sectionRequiredParameterErrorMessage(
            sectionName,
            KEY_COMMAND_VALID_IF
        )
    }
    val commands = requireNotNull(this[KEY_COMMAND_COMMANDS] as? List<Map<String, Any>>) {
        sectionRequiredParameterErrorMessage(
            sectionName,
            KEY_COMMAND_COMMANDS
        )
    }
    val elseCommands = this[KEY_COMMAND_ELSE_COMMANDS] as? List<Map<String, Any>>

    return RecipeCommand.Predicate(
        validIf = validIfString.toRecipeExpression(sectionName),
        commands = commands.map { it.toRecipeCommand("$sectionName:$KEY_COMMAND_COMMANDS") },
        elseCommands = elseCommands?.map { it.toRecipeCommand("$sectionName:$KEY_COMMAND_ELSE_COMMANDS") } ?: emptyList()
    )
}
