package ru.hh.plugins.geminio.sdk.form.expressions

import ru.hh.plugins.geminio.sdk.form.GeminioFormBooleanEvaluator
import ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand

/**
 * Converts a recipe boolean expression into a pure evaluator working on [ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext].
 */
internal fun RecipeExpression.toBooleanEvaluator(): GeminioFormBooleanEvaluator {
    val expression = this
    return { expression.evaluateBoolean(this) }
}

private fun RecipeExpression.evaluateBoolean(
    context: GeminioFormEvaluationContext,
): Boolean {
    return when (expressionCommands.size) {
        // Legacy compatibility with the old Android Studio-backed runtime.
        // TODO: tighten this after the custom runtime migration and reject empty boolean expressions.
        0 -> true
        1 -> expressionCommands[0].resolveBooleanValue(context)
        else -> throw IllegalArgumentException(
            "Unexpected commands for boolean parameter evaluation [$expressionCommands]"
        )
    }
}

private fun RecipeExpressionCommand.resolveBooleanValue(
    context: GeminioFormEvaluationContext,
): Boolean {
    return when (this) {
        is RecipeExpressionCommand.Dynamic -> {
            context.getValue(parameterId) as? Boolean
                ?: throw IllegalArgumentException(
                    "Unknown parameter or not boolean parameter for boolean expression [id: $parameterId]"
                )
        }

        RecipeExpressionCommand.ReturnTrue -> true
        RecipeExpressionCommand.ReturnFalse -> false

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
