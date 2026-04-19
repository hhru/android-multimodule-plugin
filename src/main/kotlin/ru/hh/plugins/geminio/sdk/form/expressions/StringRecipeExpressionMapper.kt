package ru.hh.plugins.geminio.sdk.form.expressions

import ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathAlias
import ru.hh.plugins.geminio.sdk.form.GeminioFormStringEvaluator
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier
import java.util.Locale

/**
 * Converts a recipe string expression into a pure evaluator working on [ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext].
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

private fun RecipeExpressionCommand.Dynamic.resolveDynamicStringValue(
    context: GeminioFormEvaluationContext,
): String {
    val value = context.getValue(parameterId) as? String
        ?: throw IllegalArgumentException(
            "Unknown parameter or not string parameter for string expression [$parameterId]"
        )

    return value.applyModifiers(modifiers)
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

private fun String.applyModifiers(modifiers: List<RecipeExpressionModifier>): String {
    var result = this

    modifiers.forEach { modifier ->
        result = when (modifier) {
            RecipeExpressionModifier.ACTIVITY_TO_LAYOUT -> {
                result.toLayoutResourceName(suffix = "Activity", prefix = "activity")
            }

            RecipeExpressionModifier.FRAGMENT_TO_LAYOUT -> {
                result.toLayoutResourceName(suffix = "Fragment", prefix = "fragment")
            }

            RecipeExpressionModifier.CLASS_TO_RESOURCE -> result.camelCaseToUnderlines()
            RecipeExpressionModifier.CAMEL_CASE_TO_UNDERLINES -> result.camelCaseToUnderlines()
            RecipeExpressionModifier.LAYOUT_TO_ACTIVITY -> {
                result.layoutToComponentName(prefix = "activity", suffix = "Activity")
            }

            RecipeExpressionModifier.LAYOUT_TO_FRAGMENT -> {
                result.layoutToComponentName(prefix = "fragment", suffix = "Fragment")
            }

            RecipeExpressionModifier.UNDERSCORE_TO_CAMEL_CASE -> result.underscoreToCamelCase()
        }
    }

    return result
}

private fun String.toLayoutResourceName(
    suffix: String,
    prefix: String,
): String {
    val baseName = removeSuffix(suffix).camelCaseToUnderlines().removePrefix("${prefix}_")
    return "${prefix}_${baseName}".trimEnd('_')
}

private fun String.layoutToComponentName(
    prefix: String,
    suffix: String,
): String {
    val rawName = removePrefix("${prefix}_")
    return rawName.underscoreToCamelCase() + suffix
}

private fun String.camelCaseToUnderlines(): String {
    return replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
        .replace(Regex("([A-Z])([A-Z][a-z])"), "$1_$2")
        .replace('-', '_')
        .replace(' ', '_')
        .lowercase(Locale.getDefault())
}

private fun String.underscoreToCamelCase(): String {
    return split('_')
        .filter { it.isNotBlank() }
        .joinToString(separator = "") { part -> part.capitalizeAscii() }
}

private fun String.capitalizeAscii(): String {
    return replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.titlecase(Locale.getDefault())
        } else {
            char.toString()
        }
    }
}
