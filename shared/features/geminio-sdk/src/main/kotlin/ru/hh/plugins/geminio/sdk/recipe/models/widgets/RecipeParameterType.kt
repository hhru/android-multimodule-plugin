package ru.hh.plugins.geminio.sdk.recipe.models.widgets


internal enum class RecipeParameterType(
    val yamlKey: String
) {

    STRING_PARAMETER("stringParameter"),
    BOOLEAN_PARAMETER("booleanParameter"),

    ;

    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }

}