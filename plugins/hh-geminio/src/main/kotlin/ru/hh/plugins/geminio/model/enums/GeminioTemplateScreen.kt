package ru.hh.plugins.geminio.model.enums


/**
 * Determines in which context (basically a screen) the template should be shown.
 * Note: [NEW_PROJECT_EXTRA_DETAIL] should only be used if [NEW_PROJECT] is simultaneously used.
 */
enum class GeminioTemplateScreen(
    val yamlKey: String
) {
    NEW_PROJECT("new_project"),

    /** Show extra step for Activity details in New Project. */
    NEW_PROJECT_EXTRA_DETAIL("new_project_extra_detail"),

    NEW_MODULE("new_module"),
    MENU_ENTRY("menu_entry"),
    ACTIVITY_GALLERY("activity_gallery"),
    FRAGMENT_GALLERY("fragment_gallery");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
    }
}