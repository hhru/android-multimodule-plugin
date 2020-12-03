@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.model.yaml

import ru.hh.plugins.model.BuildGradleDependencyType
import ru.hh.plugins.geminio.model.RecipeCommand
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_COMMANDS
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_DEPENDENCIES
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_FILE
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_FROM
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_TO
import ru.hh.plugins.geminio.model.yaml.YamlKeys.KEY_COMMAND_TYPE_FOR_ALL
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

    fun Map<String, Map<String, Any>>.toRecipeCommand(): RecipeCommand {
        val instantiateCommand = this[KEY_RECIPE_INSTANTIATE]
        val openCommand = this[KEY_RECIPE_OPEN]
        val instantiateAndOpenCommand = this[KEY_RECIPE_INSTANTIATE_AND_OPEN]
        val predicateCommand = this[KEY_RECIPE_PREDICATE]
        val addDependenciesCommand = this[KEY_RECIPE_ADD_DEPENDENCIES]

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

    private fun Map<String, Any>.toRecipeAddDependenciesCommand(): RecipeCommand.AddDependencies {
        val typeForAllYamlKey = requireNotNull(this[KEY_COMMAND_TYPE_FOR_ALL] as? String) {
            "Not found '${KEY_COMMAND_TYPE_FOR_ALL}' for '${KEY_RECIPE_ADD_DEPENDENCIES}' command (expect simple string)"
        }
        val dependenciesObjects = requireNotNull(this[KEY_COMMAND_DEPENDENCIES] as? List<Map<String, Any>>) {
            "Not found '${KEY_COMMAND_DEPENDENCIES}' for '${KEY_RECIPE_ADD_DEPENDENCIES}' command (expect list of objects or strings)"
        }

        val typeForAll = requireNotNull(BuildGradleDependencyType.fromYamlKey(typeForAllYamlKey)) {
            "Unknown yaml key for BuildGradleDependencyType in '${KEY_RECIPE_ADD_DEPENDENCIES}' command [unknown key: $typeForAllYamlKey, available keys: ${BuildGradleDependencyType.availableYamlKeys()}]"
        }
        return RecipeCommand.AddDependencies(
            dependencies = dependenciesObjects.map { dependencyObject ->
                with(buildGradleDependencyParser) { dependencyObject.toBuildGradleDependency(typeForAll) }
            }
        )
    }

    private fun String.toRecipeExpression() = expressionParser.parseExpression(this)

}