@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.widgets

import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.WidgetsSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.rootSectionErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage


private const val KEY_WIDGETS_SECTION = "widgets"

private const val KEY_STRING_PARAMETER_TYPE = "stringParameter"
private const val KEY_BOOLEAN_PARAMETER_TYPE = "booleanParameter"


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.widgets.WidgetsSection].
 */
internal fun Map<String, Any>.toWidgetsSection(): WidgetsSection {
    val widgetsList = requireNotNull(this[KEY_WIDGETS_SECTION] as? List<Map<String, Any>>) {
        rootSectionErrorMessage(KEY_WIDGETS_SECTION)
    }

    return WidgetsSection(
        parameters = widgetsList.map { it.toRecipeParameter() }
    )
}


private fun Map<String, Any>.toRecipeParameter(): RecipeParameter {
    val stringParameterMap = this[KEY_STRING_PARAMETER_TYPE] as? Map<String, Any>
    val booleanParameterMap = this[KEY_BOOLEAN_PARAMETER_TYPE] as? Map<String, Any>

    return when {
        stringParameterMap != null -> {
            stringParameterMap.toWidgetsStringParameter("${KEY_WIDGETS_SECTION}:${KEY_STRING_PARAMETER_TYPE}")
        }

        booleanParameterMap != null -> {
            booleanParameterMap.toWidgetsBooleanParameter("${KEY_WIDGETS_SECTION}:${KEY_BOOLEAN_PARAMETER_TYPE}")
        }

        else -> {
            throw IllegalArgumentException(
                sectionUnknownEnumKeyErrorMessage(
                    KEY_WIDGETS_SECTION,
                    this.keys.toString(),
                    listOf(
                        KEY_STRING_PARAMETER_TYPE,
                        KEY_BOOLEAN_PARAMETER_TYPE,
                    ).joinToString { "'$it'" }
                )
            )
        }
    }
}