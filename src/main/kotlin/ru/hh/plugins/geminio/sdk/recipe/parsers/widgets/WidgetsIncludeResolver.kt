@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.widgets

import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.rootSectionErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage
import ru.hh.plugins.utils.yaml.YamlUtils
import java.io.File

internal fun Map<String, Any>.resolveRawWidgetDefinitions(
    recipeFilePath: String? = null,
): List<RawWidgetDefinition> {
    val normalizedRecipeFilePath = recipeFilePath?.let(::normalizePath)

    return resolveRawWidgetDefinitions(
        sourceFilePath = normalizedRecipeFilePath,
        includeChain = normalizedRecipeFilePath?.let(::listOf).orEmpty(),
    )
}

private fun Map<String, Any>.resolveRawWidgetDefinitions(
    sourceFilePath: String?,
    includeChain: List<String>,
): List<RawWidgetDefinition> {
    val widgetsList = requireNotNull(this[KEY_WIDGETS_SECTION] as? List<*>) {
        rootSectionErrorMessage(KEY_WIDGETS_SECTION)
    }

    return widgetsList.flatMap { rawWidget ->
        val widgetMap = requireNotNull(rawWidget as? Map<String, Any>) {
            sectionErrorMessage(
                KEY_WIDGETS_SECTION,
                "Every widget entry should be an object.",
            )
        }

        val includeMap = widgetMap[KEY_INCLUDE_PARAMETER_TYPE] as? Map<String, Any>
        if (includeMap == null) {
            listOf(
                RawWidgetDefinition(
                    definition = widgetMap,
                    sourceFilePath = sourceFilePath,
                )
            )
        } else {
            require(widgetMap.keys == setOf(KEY_INCLUDE_PARAMETER_TYPE)) {
                sectionErrorMessage(
                    KEY_WIDGETS_SECTION,
                    "Include widget entry should declare only '$KEY_INCLUDE_PARAMETER_TYPE' " +
                        "[keys: ${widgetMap.keys}].",
                )
            }
            includeMap.resolveIncludedWidgetDefinitions(
                parentSourceFilePath = sourceFilePath,
                includeChain = includeChain,
            )
        }
    }
}

private fun Map<String, Any>.resolveIncludedWidgetDefinitions(
    parentSourceFilePath: String?,
    includeChain: List<String>,
): List<RawWidgetDefinition> {
    val sectionName = "$KEY_WIDGETS_SECTION:$KEY_INCLUDE_PARAMETER_TYPE"
    val includeFilePath = requireNotNull(this[KEY_INCLUDE_FILE] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = sectionName,
            key = KEY_INCLUDE_FILE,
        )
    }
    require(this.keys == setOf(KEY_INCLUDE_FILE)) {
        sectionErrorMessage(
            sectionName,
            "Include entry should declare only '$KEY_INCLUDE_FILE'.",
        )
    }

    val parentFile = requireNotNull(parentSourceFilePath) {
        sectionErrorMessage(
            sectionName,
            "Include resolution requires source file context.",
        )
    }
    val resolvedIncludeFile = File(File(parentFile).parentFile, includeFilePath)
    val normalizedIncludeFilePath = normalizePath(resolvedIncludeFile.path)

    require(normalizedIncludeFilePath !in includeChain) {
        val includeChainText = (includeChain + normalizedIncludeFilePath).joinToString(" -> ")
        sectionErrorMessage(
            sectionName,
            "Circular widgets include detected [chain: $includeChainText].",
        )
    }

    val includedConfigMap = YamlUtils.loadFromConfigFile(normalizedIncludeFilePath) { throwable ->
        throwable.printStackTrace()
    } ?: throw IllegalArgumentException(
        sectionErrorMessage(
            sectionName,
            "Cannot load include file [path: $normalizedIncludeFilePath].",
        )
    )

    require(includedConfigMap.keys == setOf(KEY_WIDGETS_SECTION)) {
        sectionErrorMessage(
            sectionName,
            "Included widgets file should contain only '$KEY_WIDGETS_SECTION' top-level section " +
                    "[path: $normalizedIncludeFilePath, keys: ${includedConfigMap.keys}].",
        )
    }

    return includedConfigMap.resolveRawWidgetDefinitions(
        sourceFilePath = normalizedIncludeFilePath,
        includeChain = includeChain + normalizedIncludeFilePath,
    )
}

private fun normalizePath(path: String): String {
    return File(path).canonicalFile.path
}

private const val KEY_WIDGETS_SECTION = "widgets"
private const val KEY_INCLUDE_PARAMETER_TYPE = "include"
private const val KEY_INCLUDE_FILE = "file"
