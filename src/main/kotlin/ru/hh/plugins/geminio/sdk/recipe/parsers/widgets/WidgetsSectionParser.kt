@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.widgets

import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.WidgetsSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage
import java.io.File

private const val KEY_WIDGETS_SECTION = "widgets"

private const val KEY_STRING_PARAMETER_TYPE = "stringParameter"
private const val KEY_BOOLEAN_PARAMETER_TYPE = "booleanParameter"
private const val KEY_SUGGEST_PARAMETER_TYPE = "suggestParameter"

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.widgets.WidgetsSection].
 */
internal fun Map<String, Any>.toWidgetsSection(
    recipeFilePath: String? = null,
): WidgetsSection {
    val rawWidgetDefinitions = resolveRawWidgetDefinitions(recipeFilePath)
    val parameters = rawWidgetDefinitions.map { rawWidgetDefinition ->
        rawWidgetDefinition.definition.toRecipeParameter(rawWidgetDefinition.sourceFilePath)
    }

    validateUniqueWidgetIds(parameters, rawWidgetDefinitions)

    return WidgetsSection(
        parameters = parameters
    )
}

private fun Map<String, Any>.toRecipeParameter(
    sourceFilePath: String?,
): RecipeParameter {
    val stringParameterMap = this[KEY_STRING_PARAMETER_TYPE] as? Map<String, Any>
    val booleanParameterMap = this[KEY_BOOLEAN_PARAMETER_TYPE] as? Map<String, Any>
    val suggestParameterMap = this[KEY_SUGGEST_PARAMETER_TYPE] as? Map<String, Any>
    val sourceDirPath = sourceFilePath?.let { filePath -> File(filePath).parent }

    return when {
        stringParameterMap != null -> {
            stringParameterMap.toWidgetsStringParameter("$KEY_WIDGETS_SECTION:$KEY_STRING_PARAMETER_TYPE")
        }

        booleanParameterMap != null -> {
            booleanParameterMap.toWidgetsBooleanParameter("$KEY_WIDGETS_SECTION:$KEY_BOOLEAN_PARAMETER_TYPE")
        }

        suggestParameterMap != null -> {
            suggestParameterMap.toWidgetsSuggestParameter(
                sectionName = "$KEY_WIDGETS_SECTION:$KEY_SUGGEST_PARAMETER_TYPE",
                recipeRootDirPath = sourceDirPath,
            )
        }

        else -> {
            throw IllegalArgumentException(
                sectionUnknownEnumKeyErrorMessage(
                    KEY_WIDGETS_SECTION,
                    this.keys.toString(),
                    listOf(
                        KEY_STRING_PARAMETER_TYPE,
                        KEY_BOOLEAN_PARAMETER_TYPE,
                        KEY_SUGGEST_PARAMETER_TYPE,
                    ).joinToString { "'$it'" }
                )
            )
        }
    }
}

private fun validateUniqueWidgetIds(
    parameters: List<RecipeParameter>,
    rawWidgetDefinitions: List<RawWidgetDefinition>,
) {
    val duplicatedIds = parameters
        .zip(rawWidgetDefinitions)
        .groupBy(
            keySelector = { (parameter, _) -> parameter.id },
            valueTransform = { (_, rawDefinition) -> rawDefinition.sourceFilePath ?: "<inline>" },
        )
        .filterValues { sourceFiles -> sourceFiles.size > 1 }

    require(duplicatedIds.isEmpty()) {
        val duplicateDescriptions = duplicatedIds.entries.joinToString { (parameterId, sourceFiles) ->
            "id='$parameterId', files=${sourceFiles.distinct()}"
        }
        sectionErrorMessage(
            KEY_WIDGETS_SECTION,
            "Widget parameter ids should be unique [$duplicateDescriptions].",
        )
    }
}
