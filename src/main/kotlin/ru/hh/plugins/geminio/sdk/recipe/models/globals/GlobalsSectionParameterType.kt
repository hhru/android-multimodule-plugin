package ru.hh.plugins.geminio.sdk.recipe.models.globals

/**
 * Available parameters types in recipe's globals section.
 */
internal enum class GlobalsSectionParameterType(
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
