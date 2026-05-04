package ru.hh.plugins.geminio.sdk.recipe.parsers.widgets

internal data class RawWidgetDefinition(
    val definition: Map<String, Any>,
    val sourceFilePath: String?,
)
