package ru.hh.plugins.geminio.sdk.recipe.parsers

import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.geminio.GeminioConstants
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.OptionalParams
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeParameter
import ru.hh.plugins.geminio.sdk.recipe.models.RequiredParams
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioStringParameterConstraint
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateCategory
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateConstraint
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateFormFactor
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateScreen
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_OPTIONAL_PARAMS
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_OPTIONAL_PARAMS_CATEGORY
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_OPTIONAL_PARAMS_CONSTRAINTS
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_OPTIONAL_PARAMS_FORM_FACTOR
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_OPTIONAL_PARAMS_MIN_API
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_OPTIONAL_PARAMS_MIN_BUILD_API
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_OPTIONAL_PARAMS_REVISION
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_OPTIONAL_PARAMS_SCREENS
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_PARAMETER_AVAILABILITY
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_PARAMETER_CONSTRAINTS
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_PARAMETER_DEFAULT
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_PARAMETER_HELP
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_PARAMETER_NAME
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_PARAMETER_SUGGEST
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_PARAMETER_VISIBILITY
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_RECIPE
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_REQUIRED_PARAMS
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_REQUIRED_PARAMS_DESCRIPTION
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_REQUIRED_PARAMS_NAME
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_WIDGETS
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_WIDGETS_BOOLEAN_PARAMETER
import ru.hh.plugins.geminio.sdk.recipe.parsers.YamlKeys.KEY_WIDGETS_STRING_PARAMETER
import ru.hh.plugins.utils.config.YamlUtils
import java.io.File


@Suppress("UNCHECKED_CAST")
class GeminioRecipeReader(
    private val expressionParser: GeminioRecipeExpressionParser = GeminioRecipeExpressionParser(),
    private val recipeCommandsParser: GeminioRecipeCommandsParser = GeminioRecipeCommandsParser(expressionParser)
) {

    fun parse(recipeYamlFilePath: String): GeminioRecipe {
        val configMap = YamlUtils.loadFromConfigFile(recipeYamlFilePath) { throwable ->
            throwable.printStackTrace()
        } ?: throw IllegalArgumentException("Unknown error with parsing $recipeYamlFilePath")


        return with(configMap) {
            GeminioRecipe(
                freemarkerTemplatesRootDirPath = File(recipeYamlFilePath).parent,
                requiredParams = extractRequiredParams(),
                optionalParams = extractOptionalParams(),
                recipeParameters = extractRecipeParameters(),
                recipeCommands = extractRecipeCommands()
            )
        }
    }


    private fun Map<String, Any>.extractRequiredParams(): RequiredParams {
        val requiredParamsMap = this[KEY_REQUIRED_PARAMS] as LinkedHashMap<String, Any>

        return RequiredParams(
            name = requiredParamsMap[KEY_REQUIRED_PARAMS_NAME] as String,
            description = requiredParamsMap[KEY_REQUIRED_PARAMS_DESCRIPTION] as String,
        )
    }

    private fun Map<String, Any>.extractOptionalParams(): OptionalParams? {
        val optionalParamsMap = this[KEY_OPTIONAL_PARAMS] as? LinkedHashMap<String, Any> ?: return null

        val revision = optionalParamsMap[KEY_OPTIONAL_PARAMS_REVISION] as? Int
            ?: GeminioConstants.DEFAULT_REVISION_VALUE
        val categoryYamlKey = optionalParamsMap[KEY_OPTIONAL_PARAMS_CATEGORY] as? String ?: String.EMPTY
        val formFactorYamlKey = optionalParamsMap[KEY_OPTIONAL_PARAMS_FORM_FACTOR] as? String ?: String.EMPTY
        val constraintsYamlKeys = optionalParamsMap[KEY_OPTIONAL_PARAMS_CONSTRAINTS] as? List<String> ?: emptyList()
        val screensYamlKeys = optionalParamsMap[KEY_OPTIONAL_PARAMS_SCREENS] as? List<String> ?: emptyList()
        val minApiValue = optionalParamsMap[KEY_OPTIONAL_PARAMS_MIN_API] as? Int
            ?: GeminioConstants.DEFAULT_MIN_API_VALUE
        val minBuildApiValue = optionalParamsMap[KEY_OPTIONAL_PARAMS_MIN_BUILD_API] as? Int
            ?: GeminioConstants.DEFAULT_MIN_BUILD_API_VALUE

        return OptionalParams(
            revision = revision,
            category = GeminioTemplateCategory.fromYamlKey(categoryYamlKey),
            formFactor = GeminioTemplateFormFactor.fromYamlKey(formFactorYamlKey),
            constraints = constraintsYamlKeys.mapNotNull { yamlKey ->
                GeminioTemplateConstraint.fromYamlKey(yamlKey)
            },
            screens = screensYamlKeys.mapNotNull { yamlKey ->
                GeminioTemplateScreen.fromYamlKey(yamlKey)
            },
            minApi = minApiValue,
            minBuildApi = minBuildApiValue,
        )
    }

    private fun Map<String, Any>.extractRecipeParameters(): List<RecipeParameter> {
        val widgetsMap = this[KEY_WIDGETS] as List<Map<String, Map<String, Any>>>

        return widgetsMap.map { it.toRecipeParameter() }
    }

    private fun Map<String, Map<String, Any>>.toRecipeParameter(): RecipeParameter {
        val stringParameterMap = this[KEY_WIDGETS_STRING_PARAMETER]
        val booleanParameterMap = this[KEY_WIDGETS_BOOLEAN_PARAMETER]

        return when {
            stringParameterMap != null -> stringParameterMap.toRecipeStringParameter()
            booleanParameterMap != null -> booleanParameterMap.toRecipeBooleanParameter()
            else -> throw IllegalArgumentException("Unknown parameter type")
        }
    }

    private fun Map<String, Any>.toRecipeStringParameter(): RecipeParameter.StringParameter {
        val constraintsKeys = this[KEY_PARAMETER_CONSTRAINTS] as? List<String>

        val visibilityExpressionString = getBooleanOrStringExpression(KEY_PARAMETER_VISIBILITY)
        val availabilityExpressionString = getBooleanOrStringExpression(KEY_PARAMETER_AVAILABILITY)
        val suggestExpressionString = this[KEY_PARAMETER_SUGGEST] as? String

        return RecipeParameter.StringParameter(
            id = this[KEY_PARAMETER_ID] as String,
            name = this[KEY_PARAMETER_NAME] as String,
            help = this[KEY_PARAMETER_HELP] as String,
            visibilityExpression = visibilityExpressionString?.toRecipeExpression(),
            availabilityExpression = availabilityExpressionString?.toRecipeExpression(),
            default = this[KEY_PARAMETER_DEFAULT] as? String,
            suggestExpression = suggestExpressionString?.toRecipeExpression(),
            constraints = constraintsKeys?.mapNotNull { yamlKey ->
                GeminioStringParameterConstraint.fromYamlKey(yamlKey)
            } ?: emptyList(),
        )
    }

    private fun Map<String, Any>.toRecipeBooleanParameter(): RecipeParameter.BooleanParameter {
        val visibilityExpressionString = getBooleanOrStringExpression(KEY_PARAMETER_VISIBILITY)
        val availabilityExpressionString = getBooleanOrStringExpression(KEY_PARAMETER_AVAILABILITY)

        return RecipeParameter.BooleanParameter(
            id = this[KEY_PARAMETER_ID] as String,
            name = this[KEY_PARAMETER_NAME] as String,
            help = this[KEY_PARAMETER_HELP] as String,
            visibilityExpression = visibilityExpressionString?.toRecipeExpression(),
            availabilityExpression = availabilityExpressionString?.toRecipeExpression(),
            default = this[KEY_PARAMETER_DEFAULT] as? Boolean,
        )
    }

    private fun Map<String, Any>.extractRecipeCommands(): List<RecipeCommand> {
        val recipeMap = this[KEY_RECIPE] as List<Map<String, Map<String, Any>>>

        return recipeMap.map { recipeCommandEntry ->
            with(recipeCommandsParser) { recipeCommandEntry.toRecipeCommand() }
        }
    }


    private fun String.toRecipeExpression(): RecipeExpression {
        return expressionParser.parseExpression(this)
    }

    private fun Map<String, Any>.getBooleanOrStringExpression(key: String): String? {
        return (this[key] as? Boolean)?.let { "$it" } ?: this[key] as? String
    }

}