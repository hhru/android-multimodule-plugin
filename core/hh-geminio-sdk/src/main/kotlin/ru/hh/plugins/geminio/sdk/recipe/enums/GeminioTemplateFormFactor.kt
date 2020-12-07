package ru.hh.plugins.geminio.sdk.recipe.enums


/**
 * Determines to which form factor the template belongs.
 * Templates with particular form factor may only be rendered in the project of corresponding [GeminioTemplateCategory].
 */
enum class GeminioTemplateFormFactor(
    val yamlKey: String
) {

    MOBILE("mobile"),
    WEAR("wear"),
    TV("tv"),
    AUTOMOTIVE("automotive"),
    THINGS("things"),
    GENERIC("generic");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey } ?: GENERIC
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }
}