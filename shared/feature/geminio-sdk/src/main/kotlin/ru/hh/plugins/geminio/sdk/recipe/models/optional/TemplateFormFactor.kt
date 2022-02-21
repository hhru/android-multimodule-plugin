package ru.hh.plugins.geminio.sdk.recipe.models.optional

/**
 * Determines to which form factor the template belongs.
 * Templates with particular form factor may only be rendered in the project of corresponding [TemplateCategory].
 */
enum class TemplateFormFactor(
    val yamlKey: String
) {

    MOBILE("mobile"),
    WEAR("wear"),
    TV("tv"),
    AUTOMOTIVE("automotive"),
    GENERIC("generic");

    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }
}
