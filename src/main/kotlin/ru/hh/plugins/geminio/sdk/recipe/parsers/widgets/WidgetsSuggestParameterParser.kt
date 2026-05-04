@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.widgets

import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.SuggestParameterOption
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toBooleanRecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression
import ru.hh.plugins.utils.yaml.YamlUtils.getBooleanOrStringExpression
import java.io.File

/**
 * Parser from YAML to [RecipeParameter.SuggestParameter].
 */
internal fun Map<String, Any>.toWidgetsSuggestParameter(
    sectionName: String,
    recipeRootDirPath: String?,
): RecipeParameter.SuggestParameter {
    val id = requireNotNull(this[KEY_PARAMETER_ID] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_PARAMETER_ID,
        )
    }
    val name = requireNotNull(this[KEY_PARAMETER_NAME] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_PARAMETER_NAME,
        )
    }
    val help = this[KEY_PARAMETER_HELP] as? String
    val visibilityExpressionString = this.getBooleanOrStringExpression(KEY_PARAMETER_VISIBILITY)
    val availabilityExpressionString = this.getBooleanOrStringExpression(KEY_PARAMETER_AVAILABILITY)
    val default = this[KEY_PARAMETER_DEFAULT] as? String
    val isSealed = this[KEY_PARAMETER_SEALED] as? Boolean ?: false
    val suggestExpressionString = this[KEY_PARAMETER_SUGGEST] as? String
    val options = this.toSuggestParameterOptions(
        sectionName = sectionName,
        recipeRootDirPath = recipeRootDirPath,
    )

    require(options.isNotEmpty()) {
        sectionErrorMessage(sectionName, "Suggest parameter should declare at least one option.")
    }
    require(options.distinctBy(SuggestParameterOption::value).size == options.size) {
        sectionErrorMessage(
            sectionName,
            "Suggest parameter options should have unique '$KEY_OPTION_VALUE' values " +
                "[values: ${options.map(SuggestParameterOption::value)}].",
        )
    }
    require(options.distinctBy(SuggestParameterOption::label).size == options.size) {
        sectionErrorMessage(
            sectionName,
            "Suggest parameter options should have unique '$KEY_OPTION_LABEL' values " +
                "[labels: ${options.map(SuggestParameterOption::label)}].",
        )
    }
    require(isSealed.not() || default == null || options.any { option -> option.value == default }) {
        sectionErrorMessage(
            sectionName,
            "Sealed suggest parameter default should match one of " +
                "'$KEY_PARAMETER_OPTIONS.$KEY_OPTION_VALUE' [default: $default].",
        )
    }

    return RecipeParameter.SuggestParameter(
        id = id,
        name = name,
        help = help,
        visibilityExpression = visibilityExpressionString?.toBooleanRecipeExpression(sectionName),
        availabilityExpression = availabilityExpressionString?.toBooleanRecipeExpression(sectionName),
        default = default,
        isSealed = isSealed,
        suggestExpression = suggestExpressionString?.toRecipeExpression(sectionName),
        options = options,
    )
}

private fun Map<String, Any>.toSuggestParameterOptions(
    sectionName: String,
    recipeRootDirPath: String?,
): List<SuggestParameterOption> {
    val rawOptions = requireNotNull(this[KEY_PARAMETER_OPTIONS]) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_PARAMETER_OPTIONS,
        )
    }

    return when (rawOptions) {
        is List<*> -> rawOptions.mapIndexed { index, option ->
            require(option is Map<*, *>) {
                sectionErrorMessage(
                    sectionName,
                    "Suggest parameter options should be declared as maps with '$KEY_OPTION_VALUE'.",
                )
            }
            @Suppress("UNCHECKED_CAST")
            (option as Map<String, Any>).toSuggestParameterOption(
                sectionName = sectionName,
                optionIndex = index,
            )
        }

        is Map<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            (rawOptions as Map<String, Any>).loadSuggestParameterOptionsFromSource(
                sectionName = sectionName,
                recipeRootDirPath = recipeRootDirPath,
            )
        }

        else -> throw IllegalArgumentException(
            sectionErrorMessage(
                sectionName,
                "Suggest parameter options should be either a list of values or an object with '$KEY_OPTION_SOURCE'.",
            )
        )
    }
}

private fun Map<String, Any>.loadSuggestParameterOptionsFromSource(
    sectionName: String,
    recipeRootDirPath: String?,
): List<SuggestParameterOption> {
    val source = requireNotNull(this[KEY_OPTION_SOURCE] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = "$sectionName:$KEY_PARAMETER_OPTIONS",
            key = KEY_OPTION_SOURCE,
        )
    }
    require(this.keys == setOf(KEY_OPTION_SOURCE)) {
        sectionErrorMessage(
            sectionName,
            "Suggest parameter options source should declare only '$KEY_OPTION_SOURCE'.",
        )
    }

    val recipeRootDir = requireNotNull(recipeRootDirPath) {
        sectionErrorMessage(
            sectionName,
            "Suggest parameter options source requires recipe root directory context.",
        )
    }
    val sourceFile = File(recipeRootDir, source)
    require(sourceFile.isFile) {
        sectionErrorMessage(
            sectionName,
            "Suggest parameter options source file does not exist [path: ${sourceFile.path}].",
        )
    }

    val rows = sourceFile
        .readLines(Charsets.UTF_8)
        .map(String::trim)
        .filter(String::isNotBlank)
        .mapIndexed { index, row ->
            row.toSuggestOptionCsvRow(
                sectionName = sectionName,
                rowIndex = index,
            )
        }
        .let(::dropCsvHeaderIfPresent)

    require(rows.isNotEmpty()) {
        sectionErrorMessage(
            sectionName,
            "Suggest parameter options source file should contain at least one option.",
        )
    }

    return rows.mapIndexed { index, row ->
        row.toSuggestParameterOption(
            sectionName = sectionName,
            optionIndex = index,
        )
    }
}

private fun String.toSuggestOptionCsvRow(
    sectionName: String,
    rowIndex: Int,
): Map<String, Any> {
    val columns = split(',', limit = MAX_SUPPORTED_CSV_COLUMNS + 1)
        .map(String::trim)

    require(columns.size in 1..MAX_SUPPORTED_CSV_COLUMNS) {
        sectionErrorMessage(
            sectionName,
            "Suggest parameter CSV options support only 'value' or 'value,label' rows " +
                "[row: ${rowIndex + 1}, raw: '$this'].",
        )
    }

    return buildMap {
        put(KEY_OPTION_VALUE, columns[0])
        columns.getOrNull(1)?.let { label ->
            put(KEY_OPTION_LABEL, label)
        }
    }
}

private fun dropCsvHeaderIfPresent(
    rows: List<Map<String, Any>>,
): List<Map<String, Any>> {
    val firstRow = rows.firstOrNull() ?: return rows
    val value = firstRow[KEY_OPTION_VALUE]
    val label = firstRow[KEY_OPTION_LABEL]

    val isTwoColumnsHeader = value == CSV_HEADER_VALUE && label == CSV_HEADER_LABEL
    val isSingleColumnHeader = value == CSV_HEADER_VALUE && label == null && rows.size > 1

    return if (isTwoColumnsHeader || isSingleColumnHeader) rows.drop(1) else rows
}

private fun Map<String, Any>.toSuggestParameterOption(
    sectionName: String,
    optionIndex: Int,
): SuggestParameterOption {
    val optionSectionName = "$sectionName:$KEY_PARAMETER_OPTIONS[$optionIndex]"

    val value = requireNotNull(this[KEY_OPTION_VALUE] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = optionSectionName,
            key = KEY_OPTION_VALUE,
        )
    }
    val label = this[KEY_OPTION_LABEL] as? String ?: value

    return SuggestParameterOption(
        value = value,
        label = label,
    )
}

private const val KEY_PARAMETER_ID = "id"
private const val KEY_PARAMETER_NAME = "name"
private const val KEY_PARAMETER_HELP = "help"
private const val KEY_PARAMETER_DEFAULT = "default"
private const val KEY_PARAMETER_SEALED = "sealed"
private const val KEY_PARAMETER_SUGGEST = "suggest"
private const val KEY_PARAMETER_VISIBILITY = "visibility"
private const val KEY_PARAMETER_AVAILABILITY = "availability"
private const val KEY_PARAMETER_OPTIONS = "options"
private const val KEY_OPTION_SOURCE = "source"
private const val KEY_OPTION_VALUE = "value"
private const val KEY_OPTION_LABEL = "label"
private const val CSV_HEADER_VALUE = "value"
private const val CSV_HEADER_LABEL = "label"
private const val MAX_SUPPORTED_CSV_COLUMNS = 2
