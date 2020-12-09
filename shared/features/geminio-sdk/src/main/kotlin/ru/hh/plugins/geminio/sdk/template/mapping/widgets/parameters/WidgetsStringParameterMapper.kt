package ru.hh.plugins.geminio.sdk.template.mapping.widgets.parameters

import com.android.tools.idea.wizard.template.stringParameter
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.toBooleanLambda
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.toStringLambda


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter.StringParameter]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter].
 */
internal fun RecipeParameter.StringParameter.toAndroidStudioTemplateParameter(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameter {
    val geminioParameter = this

    return stringParameter {
        name = geminioParameter.name
        help = geminioParameter.help

        default = geminioParameter.default
        constraints = geminioParameter.constraints.map { it.toAndroidStudioTemplateStringParameterConstraint() }
        visible = geminioParameter.visibilityExpression?.toBooleanLambda(existingParametersMap) ?: { true }
        enabled = geminioParameter.availabilityExpression?.toBooleanLambda(existingParametersMap) ?: { true }
        suggest = geminioParameter.suggestExpression?.toStringLambda(existingParametersMap) ?: { null }
    }
}