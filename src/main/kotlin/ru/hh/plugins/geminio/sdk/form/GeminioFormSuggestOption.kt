package ru.hh.plugins.geminio.sdk.form

/**
 * UI/runtime representation of one selectable suggested field value.
 */
internal data class GeminioFormSuggestOption(
    val value: String,
    val label: String,
) {
    override fun toString(): String {
        return label
    }
}
