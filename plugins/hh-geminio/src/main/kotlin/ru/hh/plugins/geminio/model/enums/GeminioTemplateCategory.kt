package ru.hh.plugins.geminio.model.enums


/**
 * Determines to which menu entry the template belongs.
 */
enum class GeminioTemplateCategory(
    val yamlKey: String
) {

    ACTIVITY("activity"),
    FRAGMENT("fragment"),
    APPLICATION("application"),
    FOLDER("folder"),
    UI_COMPONENT("ui_component"),
    AUTOMOTIVE("automotive"),
    XML("xml"),
    WEAR("wear"),
    AIDL("aidl"),
    WIDGET("widget"),
    GOOGLE("google"),
    COMPOSE("compose"),
    OTHER("other");


    companion object {
        fun fromYamlKey(yamlKey: String) = fromYamlKeyForValidation(yamlKey) ?: OTHER
        fun fromYamlKeyForValidation(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
    }
}