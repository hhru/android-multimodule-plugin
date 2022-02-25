package ru.hh.plugins.geminio.sdk.recipe.models.optional

/**
 * Determines in which context (basically a screen) the template should be shown.
 */
enum class TemplateScreen(
    val yamlKey: String
) {
    NEW_PROJECT("new_project"),
    NEW_MODULE("new_module"),
    MENU_ENTRY("menu_entry"),
    ACTIVITY_GALLERY("activity_gallery"),
    FRAGMENT_GALLERY("fragment_gallery");

    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }
}
