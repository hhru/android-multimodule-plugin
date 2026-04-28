package ru.hh.plugins.geminio.sdk.execution

import ru.hh.plugins.geminio.sdk.form.expressions.toBooleanEvaluator
import ru.hh.plugins.geminio.sdk.form.expressions.toStringEvaluator
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression

/**
 * Bridges recipe expressions to the pure execution context.
 *
 * The custom UI runtime and the custom execution runtime intentionally share one evaluator
 * implementation so `suggest`, `visibility`, `globals` and recipe command expressions keep the
 * same semantics.
 */
internal fun RecipeExpression.evaluateString(
    context: GeminioRecipeEvaluationContext,
): String? {
    return toStringEvaluator().invoke(context)
}

/**
 * Evaluates a recipe boolean expression against the pure execution context.
 */
internal fun RecipeExpression.evaluateBoolean(
    context: GeminioRecipeEvaluationContext,
): Boolean {
    return toBooleanEvaluator().invoke(context)
}
