package ru.hh.plugins.geminio.sdk.template.mapping.expressions

import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBooleanParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameterBooleanLambda

/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameterBooleanLambda].
 */
internal fun RecipeExpression.toBooleanLambda(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameterBooleanLambda {
    return { evaluateBoolean(existingParametersMap) }
}

/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression]
 * into simple boolean value.
 */
internal fun RecipeExpression.evaluateBoolean(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): Boolean {
    return when (expressionCommands.size) {
        0 -> true
        1 -> expressionCommands[0].resolveBooleanValue(existingParametersMap)
        else -> throw IllegalArgumentException(
            "Unexpected commands for boolean parameter evaluation [$expressionCommands]"
        )
    }
}

private fun RecipeExpressionCommand.resolveBooleanValue(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): Boolean {
    return when (this) {
        is RecipeExpressionCommand.Dynamic -> {
            val parameter = existingParametersMap[this.parameterId] as? AndroidStudioTemplateBooleanParameter
                ?: throw IllegalArgumentException(
                    "Unknown parameter or not boolean parameter for boolean expression [id: ${this.parameterId}]"
                )

            parameter.value
        }

        RecipeExpressionCommand.ReturnTrue -> {
            true
        }

        RecipeExpressionCommand.ReturnFalse -> {
            false
        }

        is RecipeExpressionCommand.Fixed,
        RecipeExpressionCommand.SrcOut,
        RecipeExpressionCommand.ResOut,
        RecipeExpressionCommand.ManifestOut,
        RecipeExpressionCommand.CurrentDirOut,
        RecipeExpressionCommand.RootOut -> {
            throw IllegalArgumentException("Unexpected command for boolean parameter [$this]")
        }
    }
}
