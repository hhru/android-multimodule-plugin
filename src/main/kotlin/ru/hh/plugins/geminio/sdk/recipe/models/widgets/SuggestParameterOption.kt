package ru.hh.plugins.geminio.sdk.recipe.models.widgets

/**
 * One selectable option for a `suggestParameter` recipe widget.
 *
 * `value` is the actual value exposed to expressions and templates, while `label` is the text
 * shown to the user in the form UI.
 */
data class SuggestParameterOption(
    val value: String,
    val label: String,
)
