package ru.hh.plugins.geminio.sdk.model.enums


/**
 * Determines in which context (basically a screen) the template should be shown.
 */
enum class GeminioTemplateScreen(
    val yamlKey: String
) {
    NEW_PROJECT("new_project"),
    NEW_MODULE("new_module"),
    MENU_ENTRY("menu_entry"),
    ACTIVITY_GALLERY("activity_gallery"),
    FRAGMENT_GALLERY("fragment_gallery");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
    }
}