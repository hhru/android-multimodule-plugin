@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.widgets

import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.StringParameterConstraint
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression
import ru.hh.plugins.utils.yaml.YamlUtils.getBooleanOrStringExpression


private const val KEY_PARAMETER_ID = "id"
private const val KEY_PARAMETER_NAME = "name"
private const val KEY_PARAMETER_HELP = "help"
private const val KEY_PARAMETER_CONSTRAINTS = "constraints"
private const val KEY_PARAMETER_DEFAULT = "default"
private const val KEY_PARAMETER_SUGGEST = "suggest"
private const val KEY_PARAMETER_VISIBILITY = "visibility"
private const val KEY_PARAMETER_AVAILABILITY = "availability"


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter.StringParameter].
 */
internal fun Map<String, Any>.toWidgetsStringParameter(sectionName: String): RecipeParameter.StringParameter {
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
    val help = requireNotNull(this[KEY_PARAMETER_HELP] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_PARAMETER_HELP
        )
    }
    val visibilityExpressionString = this.getBooleanOrStringExpression(KEY_PARAMETER_VISIBILITY)
    val availabilityExpressionString = this.getBooleanOrStringExpression(KEY_PARAMETER_AVAILABILITY)

    val default = this[KEY_PARAMETER_DEFAULT] as? String
    val suggestExpressionString = this[KEY_PARAMETER_SUGGEST] as? String
    val constraintsKeys = this[KEY_PARAMETER_CONSTRAINTS] as? List<String>

    return RecipeParameter.StringParameter(
        id = id,
        name = name,
        help = help,
        visibilityExpression = visibilityExpressionString?.toRecipeExpression(sectionName),
        availabilityExpression = availabilityExpressionString?.toRecipeExpression(sectionName),
        default = default,
        suggestExpression = suggestExpressionString?.toRecipeExpression(sectionName),
        constraints = constraintsKeys?.map { it.toStringParameterConstraint() } ?: emptyList()
    )
}

private fun String.toStringParameterConstraint(): StringParameterConstraint {
    return requireNotNull(StringParameterConstraint.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = this,
            key = this,
            acceptableValues = StringParameterConstraint.availableYamlKeys()
        )
    }
}