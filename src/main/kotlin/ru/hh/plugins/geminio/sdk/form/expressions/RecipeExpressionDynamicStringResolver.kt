package ru.hh.plugins.geminio.sdk.form.expressions

import ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier

internal fun RecipeExpressionCommand.Dynamic.resolveDynamicStringValue(
    context: GeminioFormEvaluationContext,
): String {
    val value = context.getValue(parameterId) as? String
        ?: throw IllegalArgumentException(
            "Unknown parameter or not string parameter for string expression [$parameterId]"
        )

    return value.applyModifiers(modifiers)
}

private fun String.applyModifiers(modifiers: List<RecipeExpressionModifier>): String {
    var result = this

    modifiers.forEach { modifier ->
        result = when (modifier) {
            RecipeExpressionModifier.ACTIVITY_TO_LAYOUT -> result.activityToLayout()
            RecipeExpressionModifier.FRAGMENT_TO_LAYOUT -> result.fragmentToLayout()
            RecipeExpressionModifier.CLASS_TO_RESOURCE -> result.classToResource()
            RecipeExpressionModifier.CAMEL_CASE_TO_UNDERLINES -> result.camelCaseToUnderlines()
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
    return "${layoutPrefix}_${normalized.camelCaseToUnderlines()}"
}

private fun String.classToResource(): String {
    var result = this

    listOf(ACTIVITY_KEYWORD, FRAGMENT_KEYWORD, SERVICE_KEYWORD, PROVIDER_KEYWORD)
        .forEach { suffix ->
            result = result.removeComponentSuffix(suffix)
        }

    return result.camelCaseToUnderlines()
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
    return (length downTo MIN_COMPONENT_KEYWORD_PART_LENGTH)
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
    return (length downTo MIN_COMPONENT_KEYWORD_PART_LENGTH)
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

private fun String.camelCaseToUnderlines(): String {
    if (isEmpty()) {
        return ""
    }

    val normalized = replace('-', '_').replace(' ', '_')
    val builder = StringBuilder()

    normalized.forEachIndexed { index, char ->
        when {
            char == '_' -> {
                if (builder.isNotEmpty() && builder.last() != '_') {
                    builder.append('_')
                }
            }

            char.isUpperCase() -> {
                if (index != 0 && builder.isNotEmpty() && builder.last() != '_') {
                    builder.append('_')
                }
                builder.append(char.lowercaseChar())
            }

            else -> builder.append(char.lowercaseChar())
        }
    }

    return builder.toString().trim('_')
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

private const val MIN_COMPONENT_KEYWORD_PART_LENGTH = 2

private const val ACTIVITY_KEYWORD = "Activity"
private const val FRAGMENT_KEYWORD = "Fragment"
private const val SERVICE_KEYWORD = "Service"
private const val PROVIDER_KEYWORD = "Provider"
private const val ACTIVITY_LAYOUT_PREFIX = "activity"
private const val FRAGMENT_LAYOUT_PREFIX = "fragment"
private const val ACTIVITY_LAYOUT_RESOURCE_PREFIX = "activity_"
private const val FRAGMENT_LAYOUT_RESOURCE_PREFIX = "fragment_"
