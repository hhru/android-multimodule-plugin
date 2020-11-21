package ru.hh.plugins.geminio.model.yaml

import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.geminio.GeminioConstants
import ru.hh.plugins.geminio.model.GeminioRecipe
import ru.hh.plugins.geminio.model.GeminioStringParameterConstraint
import ru.hh.plugins.geminio.model.GeminioTemplateCategory
import ru.hh.plugins.geminio.model.GeminioTemplateConstraint
import ru.hh.plugins.geminio.model.GeminioTemplateFormFactor
import ru.hh.plugins.geminio.model.GeminioTemplateScreen
import ru.hh.plugins.utils.config.YamlUtils


@Suppress("UNCHECKED_CAST")
class GeminioRecipeReader(
    private val geminioRecipeExpressionReader: GeminioRecipeExpressionReader = GeminioRecipeExpressionReader()
) {

    companion object {
        private const val KEY_REQUIRED_PARAMS = "requiredParams"
        private const val KEY_REQUIRED_PARAMS_REVISION = "revision"
        private const val KEY_REQUIRED_PARAMS_NAME = "name"
        private const val KEY_REQUIRED_PARAMS_DESCRIPTION = "description"

        private const val KEY_OPTIONAL_PARAMS = "optionalParams"
        private const val KEY_OPTIONAL_PARAMS_CATEGORY = "category"
        private const val KEY_OPTIONAL_PARAMS_FORM_FACTOR = "formFactor"
        private const val KEY_OPTIONAL_PARAMS_CONSTRAINTS = "constraints"
        private const val KEY_OPTIONAL_PARAMS_SCREENS = "screens"
        private const val KEY_OPTIONAL_PARAMS_MIN_API = "minApi"
        private const val KEY_OPTIONAL_PARAMS_MIN_BUILD_API = "minBuildApi"

        private const val KEY_WIDGETS = "widgets"
        private const val KEY_WIDGETS_STRING_PARAMETER = "stringParameter"
        private const val KEY_WIDGETS_BOOLEAN_PARAMETER = "booleanParameter"

        private const val KEY_PARAMETER_ID = "id"
        private const val KEY_PARAMETER_NAME = "name"
        private const val KEY_PARAMETER_HELP = "help"
        private const val KEY_PARAMETER_CONSTRAINTS = "constraints"
        private const val KEY_PARAMETER_DEFAULT = "default"
        private const val KEY_PARAMETER_SUGGEST = "suggest"
        private const val KEY_PARAMETER_VISIBILITY = "visibility"
        private const val KEY_PARAMETER_AVAILABILITY = "availability"
        // private const val KEY_WIDGETS_ENUM_PARAMETER = "enumParameter" // TODO: add support?

        private const val KEY_RECIPE = "recipe"
        private const val KEY_RECIPE_INSTANTIATE = "instantiate"
        private const val KEY_RECIPE_OPEN = "open"
        private const val KEY_RECIPE_PREDICATE = "predicate"

        private const val KEY_COMMAND_FROM = "from"
        private const val KEY_COMMAND_TO = "to"
        private const val KEY_COMMAND_FILE = "file"
        private const val KEY_COMMAND_VALID_IF = "validIf"
        private const val KEY_COMMAND_COMMANDS = "commands"
    }


    fun parse(recipeYamlFilePath: String): GeminioRecipe {
        val configMap = YamlUtils.loadFromConfigFile(recipeYamlFilePath) { throwable ->
            throwable.printStackTrace()
        } ?: throw IllegalArgumentException() // todo (signal about error)


        return with(configMap) {
            GeminioRecipe(
                requiredParams = extractRequiredParams(),
                optionalParams = extractOptionalParams(),
                recipeParameters = extractRecipeParameters(),
                recipeCommands = extractRecipeCommands()
            )
        }
    }


    private fun Map<String, Any>.extractRequiredParams(): GeminioRecipe.RequiredParams {
        val requiredParamsMap = this[KEY_REQUIRED_PARAMS] as LinkedHashMap<String, Any>

        return GeminioRecipe.RequiredParams(
            revision = requiredParamsMap[KEY_REQUIRED_PARAMS_REVISION] as Int,
            name = requiredParamsMap[KEY_REQUIRED_PARAMS_NAME] as String,
            description = requiredParamsMap[KEY_REQUIRED_PARAMS_DESCRIPTION] as String,
        )
    }

    private fun Map<String, Any>.extractOptionalParams(): GeminioRecipe.OptionalParams? {
        val optionalParamsMap = this[KEY_OPTIONAL_PARAMS] as? LinkedHashMap<String, Any> ?: return null

        val categoryYamlKey = optionalParamsMap[KEY_OPTIONAL_PARAMS_CATEGORY] as? String ?: String.EMPTY
        val formFactorYamlKey = optionalParamsMap[KEY_OPTIONAL_PARAMS_FORM_FACTOR] as? String ?: String.EMPTY
        val constraintsYamlKeys = optionalParamsMap[KEY_OPTIONAL_PARAMS_CONSTRAINTS] as? List<String> ?: emptyList()
        val screensYamlKeys = optionalParamsMap[KEY_OPTIONAL_PARAMS_SCREENS] as? List<String> ?: emptyList()
        val minApiValue = optionalParamsMap[KEY_OPTIONAL_PARAMS_MIN_API] as? Int
            ?: GeminioConstants.DEFAULT_MIN_API_VALUE
        val minBuildApiValue = optionalParamsMap[KEY_OPTIONAL_PARAMS_MIN_BUILD_API] as? Int
            ?: GeminioConstants.DEFAULT_MIN_BUILD_API_VALUE

        return GeminioRecipe.OptionalParams(
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

    private fun Map<String, Any>.extractRecipeParameters(): List<GeminioRecipe.RecipeParameter> {
        val widgetsMap = this[KEY_WIDGETS] as List<Map<String, Map<String, Any>>>

        return widgetsMap.map { it.toRecipeParameter() }
    }

    private fun Map<String, Map<String, Any>>.toRecipeParameter(): GeminioRecipe.RecipeParameter {
        val stringParameterMap = this[KEY_WIDGETS_STRING_PARAMETER]
        val booleanParameterMap = this[KEY_WIDGETS_BOOLEAN_PARAMETER]

        return when {
            stringParameterMap != null -> stringParameterMap.toRecipeStringParameter()
            booleanParameterMap != null -> booleanParameterMap.toRecipeBooleanParameter()
            else -> throw IllegalArgumentException("Unknown parameter type")
        }
    }

    private fun Map<String, Any>.toRecipeStringParameter(): GeminioRecipe.RecipeParameter.StringParameter {
        val constraintsKeys = this[KEY_PARAMETER_CONSTRAINTS] as? List<String>

        val visibilityExpressionString = this[KEY_PARAMETER_VISIBILITY] as? String
        val availabilityExpressionString = this[KEY_PARAMETER_AVAILABILITY] as? String
        val suggestExpressionString = this[KEY_PARAMETER_SUGGEST] as? String

        return GeminioRecipe.RecipeParameter.StringParameter(
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

    private fun Map<String, Any>.toRecipeBooleanParameter(): GeminioRecipe.RecipeParameter.BooleanParameter {
        val visibilityExpressionString = this[KEY_PARAMETER_VISIBILITY] as? String
        val availabilityExpressionString = this[KEY_PARAMETER_AVAILABILITY] as? String

        return GeminioRecipe.RecipeParameter.BooleanParameter(
            id = this[KEY_PARAMETER_ID] as String,
            name = this[KEY_PARAMETER_NAME] as String,
            help = this[KEY_PARAMETER_HELP] as String,
            visibilityExpression = visibilityExpressionString?.toRecipeExpression(),
            availabilityExpression = availabilityExpressionString?.toRecipeExpression(),
            default = this[KEY_PARAMETER_DEFAULT] as? Boolean,
        )
    }

    private fun Map<String, Any>.extractRecipeCommands(): List<GeminioRecipe.RecipeCommand> {
        val recipeMap = this[KEY_RECIPE] as List<Map<String, Map<String, Any>>>

        return recipeMap.map { it.toRecipeCommand() }
    }

    private fun Map<String, Map<String, Any>>.toRecipeCommand(): GeminioRecipe.RecipeCommand {
        val instantiateCommand = this[KEY_RECIPE_INSTANTIATE]
        val openCommand = this[KEY_RECIPE_OPEN]
        val predicateCommand = this[KEY_RECIPE_PREDICATE]

        return when {
            instantiateCommand != null -> instantiateCommand.toRecipeInstantiateCommand()
            openCommand != null -> openCommand.toRecipeOpenCommand()
            predicateCommand != null -> predicateCommand.toRecipePredicateCommand()
            else -> throw IllegalArgumentException("Unknown parameter type")
        }
    }

    private fun Map<String, Any>.toRecipeInstantiateCommand(): GeminioRecipe.RecipeCommand.Instantiate {
        val fromString = this[KEY_COMMAND_FROM] as String
        val toString = this[KEY_COMMAND_TO] as String

        return GeminioRecipe.RecipeCommand.Instantiate(
            from = fromString.toRecipeExpression(),
            to = toString.toRecipeExpression(),
        )
    }

    private fun Map<String, Any>.toRecipeOpenCommand(): GeminioRecipe.RecipeCommand.Open {
        val fileString = this[KEY_COMMAND_FILE] as String

        return GeminioRecipe.RecipeCommand.Open(
            file = fileString.toRecipeExpression(),
        )
    }

    private fun Map<String, Any>.toRecipePredicateCommand(): GeminioRecipe.RecipeCommand.Predicate {
        val commands = this[KEY_COMMAND_COMMANDS] as List<Map<String, Map<String, Any>>>

        val validIfString = this[KEY_COMMAND_VALID_IF] as String

        return GeminioRecipe.RecipeCommand.Predicate(
            validIf = validIfString.toRecipeExpression(),
            commands = commands.map { it.toRecipeCommand() },
        )
    }


    private fun String.toRecipeExpression(): GeminioRecipe.RecipeExpression {
        return geminioRecipeExpressionReader.parseExpression(this)
    }


}