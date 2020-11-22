package ru.hh.plugins.geminio.model.mapping

import com.android.tools.idea.wizard.template.booleanParameter
import com.android.tools.idea.wizard.template.stringParameter
import ru.hh.plugins.geminio.model.temp_data.GeminioIdParameterPair
import ru.hh.plugins.geminio.model.GeminioRecipe
import ru.hh.plugins.geminio.model.aliases.AndroidStudioTemplateParameter


fun GeminioRecipe.RecipeParameter.toAndroidStudioTemplateIdParameterPair(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): GeminioIdParameterPair {
    val androidStudioTemplateParameter = when (this) {
        is GeminioRecipe.RecipeParameter.StringParameter -> {
            this.toAndroidStudioTemplateParameter(existingParametersMap)
        }

        is GeminioRecipe.RecipeParameter.BooleanParameter -> {
            this.toAndroidStudioTemplateParameter(existingParametersMap)
        }

        is GeminioRecipe.RecipeParameter.EnumParameter<*> -> {
            throw UnsupportedOperationException("Not supported enum parameters yet")
        }
    }

    return GeminioIdParameterPair(
        id = this.id,
        parameter = androidStudioTemplateParameter
    )
}


private fun GeminioRecipe.RecipeParameter.StringParameter.toAndroidStudioTemplateParameter(
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
        suggest = geminioParameter.availabilityExpression?.toStringLambda(existingParametersMap) ?: { null }
    }
}

private fun GeminioRecipe.RecipeParameter.BooleanParameter.toAndroidStudioTemplateParameter(
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