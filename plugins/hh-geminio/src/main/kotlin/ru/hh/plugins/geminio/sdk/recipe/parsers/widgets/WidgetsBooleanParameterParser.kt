package ru.hh.plugins.geminio.sdk.recipe.parsers.widgets

import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression
import ru.hh.plugins.utils.yaml.YamlUtils.getBooleanOrStringExpression

private const val KEY_PARAMETER_ID = "id"
private const val KEY_PARAMETER_NAME = "name"
private const val KEY_PARAMETER_HELP = "help"
private const val KEY_PARAMETER_VISIBILITY = "visibility"
private const val KEY_PARAMETER_AVAILABILITY = "availability"
private const val KEY_PARAMETER_DEFAULT = "default"

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter.BooleanParameter].
 */
internal fun Map<String, Any>.toWidgetsBooleanParameter(sectionName: String): RecipeParameter.BooleanParameter {
    val id = requireNotNull(this[KEY_PARAMETER_ID] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_PARAMETER_ID
        )
    }
    val name = requireNotNull(this[KEY_PARAMETER_NAME] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_PARAMETER_NAME
        )
    }
    val help = this[KEY_PARAMETER_HELP] as? String
    val visibilityExpressionString = this.getBooleanOrStringExpression(KEY_PARAMETER_VISIBILITY)
    val availabilityExpressionString = this.getBooleanOrStringExpression(KEY_PARAMETER_AVAILABILITY)

    val default = this[KEY_PARAMETER_DEFAULT] as? Boolean

    return RecipeParameter.BooleanParameter(
        id = id,
        name = name,
        help = help,
        visibilityExpression = visibilityExpressionString?.toRecipeExpression(sectionName),
        availabilityExpression = availabilityExpressionString?.toRecipeExpression(sectionName),
        default = default,
    )
}
