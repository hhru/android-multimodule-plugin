package ru.hh.plugins.geminio.sdk.form.expressions

import ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathAlias
import ru.hh.plugins.geminio.sdk.form.GeminioFormStringEvaluator
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand

/**
 * Converts a recipe string expression into a pure evaluator working on
 * [ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext].
 */
internal fun RecipeExpression.toStringEvaluator(): GeminioFormStringEvaluator {
    val expression = this
    return { expression.evaluateString(this) }
}

private fun RecipeExpression.evaluateString(
    context: GeminioFormEvaluationContext,
): String? {
    val result = buildString {
        expressionCommands.forEach { command ->
            append(command.toStringValue(context))
        }
    }

    return result.takeIf { it.isNotEmpty() }
}

private fun RecipeExpressionCommand.toStringValue(
    context: GeminioFormEvaluationContext,
): String {
    return when (this) {
        is RecipeExpressionCommand.Fixed -> value
        is RecipeExpressionCommand.Dynamic -> resolveDynamicStringValue(context)
        is RecipeExpressionCommand.EqualTo,
        is RecipeExpressionCommand.NotEqualTo -> {
            throw IllegalArgumentException("Unexpected command for string parameter [$this]")
        }
        RecipeExpressionCommand.SrcOut -> context.requirePath(GeminioFormPathAlias.SRC_OUT)
        RecipeExpressionCommand.ResOut -> context.requirePath(GeminioFormPathAlias.RES_OUT)
        RecipeExpressionCommand.ManifestOut -> context.requirePath(GeminioFormPathAlias.MANIFEST_OUT)
        RecipeExpressionCommand.RootOut -> context.requirePath(GeminioFormPathAlias.ROOT_OUT)
        RecipeExpressionCommand.CurrentDirOut -> context.requireCurrentDirPath()
        RecipeExpressionCommand.ReturnTrue,
        RecipeExpressionCommand.ReturnFalse -> {
            throw IllegalArgumentException("Unexpected command for string parameter [$this]")
        }
    }
}

private fun GeminioFormEvaluationContext.requirePath(pathAlias: GeminioFormPathAlias): String {
    val rawPath = getPath(pathAlias)
        ?: throw IllegalArgumentException("Path [$pathAlias] is not available in current form context")

    return if (rawPath.endsWith('/')) rawPath else "$rawPath/"
}

private fun GeminioFormEvaluationContext.requireCurrentDirPath(): String {
    return getPath(GeminioFormPathAlias.CURRENT_DIR_OUT)
        ?: throw IllegalArgumentException("Path [${GeminioFormPathAlias.CURRENT_DIR_OUT}] is not available in current form context")
}
