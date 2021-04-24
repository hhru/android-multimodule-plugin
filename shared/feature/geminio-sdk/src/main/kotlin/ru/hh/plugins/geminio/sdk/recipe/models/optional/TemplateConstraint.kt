package ru.hh.plugins.geminio.sdk.recipe.models.optional


/**
 * Conditions under which the template may be rendered.
 * For example, some templates only support AndroidX.
 */
enum class TemplateConstraint(
    val yamlKey: String
) {

    ANDROIDX("androidx"),
    KOTLIN("kotlin");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }
}