@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.model.yaml

import ru.hh.plugins.geminio.sdk.model.recipe.RecipeCommand
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_COMMANDS
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_FILE
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_FROM
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_TO
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_VALID_IF
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_RECIPE
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_RECIPE_ADD_DEPENDENCIES
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_RECIPE_INSTANTIATE
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_RECIPE_INSTANTIATE_AND_OPEN
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_RECIPE_OPEN
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_RECIPE_PREDICATE
import ru.hh.plugins.utils.config.YamlUtils.getBooleanOrStringExpression


/**
 * Parser for [ru.hh.plugins.geminio.model.RecipeCommand].
 */
class GeminioRecipeCommandsParser(
    private val expressionParser: GeminioRecipeExpressionParser = GeminioRecipeExpressionParser(),
    private val buildGradleDependencyParser: BuildGradleDependencyParser = BuildGradleDependencyParser()
) {

    fun Map<String, Any>.toRecipeCommand(): RecipeCommand {
        val instantiateCommand = this[KEY_RECIPE_INSTANTIATE] as? Map<String, Any>
        val openCommand = this[KEY_RECIPE_OPEN] as? Map<String, Any>
        val instantiateAndOpenCommand = this[KEY_RECIPE_INSTANTIATE_AND_OPEN] as? Map<String, Any>
        val predicateCommand = this[KEY_RECIPE_PREDICATE] as? Map<String, Any>
        val addDependenciesCommand = this[KEY_RECIPE_ADD_DEPENDENCIES] as? List<Map<String, String>>

        return when {
            instantiateCommand != null -> instantiateCommand.toRecipeInstantiateCommand()
            openCommand != null -> openCommand.toRecipeOpenCommand()
            instantiateAndOpenCommand != null -> instantiateAndOpenCommand.toRecipeInstantiateAndOpenCommand()
            predicateCommand != null -> predicateCommand.toRecipePredicateCommand()
            addDependenciesCommand != null -> addDependenciesCommand.toRecipeAddDependenciesCommand()
            else -> throw IllegalArgumentException("Unknown recipe command! Check '${KEY_RECIPE}' section.")
        }
    }


    private fun Map<String, Any>.toRecipeInstantiateCommand(): RecipeCommand.Instantiate {
        val fromString = this[KEY_COMMAND_FROM] as String
        val toString = this[KEY_COMMAND_TO] as String

        return RecipeCommand.Instantiate(
            from = fromString.toRecipeExpression(),
            to = toString.toRecipeExpression(),
        )
    }

    private fun Map<String, Any>.toRecipeOpenCommand(): RecipeCommand.Open {
        val fileString = this[KEY_COMMAND_FILE] as String

        return RecipeCommand.Open(
            file = fileString.toRecipeExpression(),
        )
    }

    private fun Map<String, Any>.toRecipeInstantiateAndOpenCommand(): RecipeCommand.InstantiateAndOpen {
        val fromString = this[KEY_COMMAND_FROM] as String
        val toString = this[KEY_COMMAND_TO] as String

        return RecipeCommand.InstantiateAndOpen(
            from = fromString.toRecipeExpression(),
            to = toString.toRecipeExpression(),
        )
    }

    private fun Map<String, Any>.toRecipePredicateCommand(): RecipeCommand.Predicate {
        val commands = this[KEY_COMMAND_COMMANDS] as List<Map<String, Map<String, Any>>>

        val validIfString = requireNotNull(getBooleanOrStringExpression(KEY_COMMAND_VALID_IF)) {
            "Not found '$KEY_COMMAND_VALID_IF' expression for '${KEY_RECIPE_PREDICATE}' command"
        }

        return RecipeCommand.Predicate(
            validIf = validIfString.toRecipeExpression(),
            commands = commands.map { it.toRecipeCommand() },
        )
    }

    private fun List<Map<String, String>>.toRecipeAddDependenciesCommand(): RecipeCommand.AddDependencies {
        return RecipeCommand.AddDependencies(
            dependencies = this.map { dependencyObject ->
                with(buildGradleDependencyParser) { dependencyObject.toBuildGradleDependency() }
            }
        )
    }

    private fun String.toRecipeExpression() = expressionParser.parseExpression(this)

}