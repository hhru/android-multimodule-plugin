package ru.hh.plugins.geminio.model.mapping

import com.android.tools.idea.wizard.template.booleanParameter
import com.android.tools.idea.wizard.template.stringParameter
import ru.hh.plugins.geminio.model.temp_data.GeminioIdParameterPair
import ru.hh.plugins.geminio.sdk.model.recipe.RecipeParameter
import ru.hh.plugins.geminio.model.aliases.AndroidStudioTemplateParameter


fun RecipeParameter.toAndroidStudioTemplateIdParameterPair(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): GeminioIdParameterPair {
    val androidStudioTemplateParameter = when (this) {
        is RecipeParameter.StringParameter -> {
            this.toAndroidStudioTemplateParameter(existingParametersMap)
        }

        is RecipeParameter.BooleanParameter -> {
            this.toAndroidStudioTemplateParameter(existingParametersMap)
        }

        is RecipeParameter.EnumParameter<*> -> {
            throw UnsupportedOperationException("Not supported enum parameters yet")
        }
    }

    return GeminioIdParameterPair(
        id = this.id,
        parameter = androidStudioTemplateParameter
    )
}


private fun RecipeParameter.StringParameter.toAndroidStudioTemplateParameter(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameter {
    val geminioParameter = this

    return stringParameter {
        name = geminioParameter.name
        help = geminioParameter.help

        default = geminioParameter.default
        constraints = geminioParameter.constraints.map { it.toAndroidStudioStringParameterConstraint() }
        visible = geminioParameter.visibilityExpression?.toBooleanLambda(existingParametersMap) ?: { true }
        enabled = geminioParameter.availabilityExpression?.toBooleanLambda(existingParametersMap) ?: { true }
        suggest = geminioParameter.suggestExpression?.toStringLambda(existingParametersMap) ?: { null }
    }
}

private fun RecipeParameter.BooleanParameter.toAndroidStudioTemplateParameter(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameter {
    val geminioParameter = this

    return booleanParameter {
        name = geminioParameter.name
        help = geminioParameter.help

        default = geminioParameter.default
        visible = geminioParameter.visibilityExpression?.toBooleanLambda(existingParametersMap) ?: { true }
        enabled = geminioParameter.availabilityExpression?.toBooleanLambda(existingParametersMap) ?: { true }
    }
}