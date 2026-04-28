package ru.hh.plugins.geminio.sdk.form.expressions

import ru.hh.plugins.extensions.fromCamelCaseToUnderlines
import ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathAlias
import ru.hh.plugins.geminio.sdk.form.GeminioFormStringEvaluator
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier

private const val ACTIVITY_KEYWORD = "Activity"
private const val FRAGMENT_KEYWORD = "Fragment"
private const val SERVICE_KEYWORD = "Service"
private const val PROVIDER_KEYWORD = "Provider"
private const val ACTIVITY_LAYOUT_PREFIX = "activity"
private const val FRAGMENT_LAYOUT_PREFIX = "fragment"
private const val ACTIVITY_LAYOUT_RESOURCE_PREFIX = "activity_"
private const val FRAGMENT_LAYOUT_RESOURCE_PREFIX = "fragment_"

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
            RecipeExpressionModifier.ACTIVITY_TO_LAYOUT -> result.activityToLayout()
            RecipeExpressionModifier.FRAGMENT_TO_LAYOUT -> result.fragmentToLayout()
            RecipeExpressionModifier.CLASS_TO_RESOURCE -> result.classToResource()
            RecipeExpressionModifier.CAMEL_CASE_TO_UNDERLINES -> result.fromCamelCaseToUnderlines()
            RecipeExpressionModifier.LAYOUT_TO_ACTIVITY -> result.layoutToActivity()
            RecipeExpressionModifier.LAYOUT_TO_FRAGMENT -> result.layoutToFragment()
            RecipeExpressionModifier.UNDERSCORE_TO_CAMEL_CASE -> result.underscoreToCamelCase()
        }
    }

    return result
}

private fun String.activityToLayout(): String {
    return componentToLayoutResourceName(
        componentKeyword = ACTIVITY_KEYWORD,
        layoutPrefix = ACTIVITY_LAYOUT_PREFIX,
    )
}

private fun String.fragmentToLayout(): String {
    return componentToLayoutResourceName(
        componentKeyword = FRAGMENT_KEYWORD,
        layoutPrefix = FRAGMENT_LAYOUT_PREFIX,
    )
}

private fun String.componentToLayoutResourceName(
    componentKeyword: String,
    layoutPrefix: String,
): String {
    if (isEmpty()) {
        return ""
    }

    val normalized = stripComponentMarker(componentKeyword)
    return "${layoutPrefix}_${normalized.fromCamelCaseToUnderlines()}"
}

private fun String.classToResource(): String {
    var result = this

    listOf(ACTIVITY_KEYWORD, FRAGMENT_KEYWORD, SERVICE_KEYWORD, PROVIDER_KEYWORD)
        .forEach { suffix ->
            result = result.removeComponentSuffix(suffix)
        }

    return result.fromCamelCaseToUnderlines()
}

private fun String.layoutToActivity(): String {
    return layoutToComponentName(
        expectedPrefix = ACTIVITY_LAYOUT_RESOURCE_PREFIX,
        componentSuffix = ACTIVITY_KEYWORD,
    )
}

private fun String.layoutToFragment(): String {
    return layoutToComponentName(
        expectedPrefix = FRAGMENT_LAYOUT_RESOURCE_PREFIX,
        componentSuffix = FRAGMENT_KEYWORD,
    )
}

private fun String.layoutToComponentName(
    expectedPrefix: String,
    componentSuffix: String,
): String {
    val rawName = removePrefix(expectedPrefix)
    val componentName = rawName.underscoreToCamelCase()
        .ifEmpty { "Main" }

    return componentName + componentSuffix
}

private fun String.stripComponentMarker(componentKeyword: String): String {
    return when {
        removeComponentSuffix(componentKeyword) != this -> removeComponentSuffix(componentKeyword)
        removeComponentPrefix(componentKeyword) != this -> removeComponentPrefix(componentKeyword)
        else -> this
    }
}

private fun String.removeComponentSuffix(componentKeyword: String): String {
    val matchedLength = matchedComponentKeywordLengthAtEnd(componentKeyword)
    return if (matchedLength != null) {
        dropLast(matchedLength)
    } else {
        this
    }
}

private fun String.removeComponentPrefix(componentKeyword: String): String {
    val matchedLength = matchedComponentKeywordLengthAtStart(componentKeyword)
    return if (matchedLength != null) {
        drop(matchedLength)
    } else {
        this
    }
}

private fun String.matchedComponentKeywordLengthAtEnd(componentKeyword: String): Int? {
    return (length downTo 2)
        .firstOrNull { candidateLength ->
            componentKeyword.length >= candidateLength &&
                    regionMatches(
                        this.length - candidateLength,
                        componentKeyword,
                        0,
                        candidateLength,
                        ignoreCase = true,
                    )
        }
}

private fun String.matchedComponentKeywordLengthAtStart(componentKeyword: String): Int? {
    return (length downTo 2)
        .firstOrNull { candidateLength ->
            componentKeyword.length >= candidateLength &&
                    componentKeyword.regionMatches(
                        0,
                        this,
                        0,
                        candidateLength,
                        ignoreCase = true,
                    )
        }
}

private fun String.underscoreToCamelCase(): String {
    return split('_')
        .filter { it.isNotBlank() }
        .joinToString(separator = "") { part -> part.capitalizeAscii() }
}

private fun String.capitalizeAscii(): String {
    return replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.uppercaseChar().toString()
        } else {
            char.toString()
        }
    }
}
