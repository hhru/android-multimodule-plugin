package ru.hh.plugins.geminio.model


/**
 * Conditions under which the template may be rendered.
 * For example, some templates only support AndroidX.
 */
enum class GeminioTemplateConstraint(
    val yamlKey: String
) {

    ANDROIDX("androidx"),
    KOTLIN("kotlin");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
    }
}