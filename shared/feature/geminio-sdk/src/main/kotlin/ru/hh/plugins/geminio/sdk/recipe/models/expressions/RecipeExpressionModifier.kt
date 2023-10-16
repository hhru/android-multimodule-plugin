package ru.hh.plugins.geminio.sdk.recipe.models.expressions

enum class RecipeExpressionModifier(
    val yamlKey: String
) {

    /** Converts an Activity class name into a suitable layout name. */
    ACTIVITY_TO_LAYOUT("activityToLayout"),

    /** Converts a Fragment class name into a suitable layout name. */
    FRAGMENT_TO_LAYOUT("fragmentToLayout"),

    /** Similar to [CAMEL_CASE_TO_UNDERLINES], but strips off common class suffixes such as "Activity", "Fragment", etc. */
    CLASS_TO_RESOURCE("classToResource"),

    CAMEL_CASE_TO_UNDERLINES("camelCaseToUnderlines"),

    LAYOUT_TO_ACTIVITY("layoutToActivity"),

    LAYOUT_TO_FRAGMENT("layoutToFragment"),

    /**
     * Make string uncapped first
     */
    UNCAP_FIRST("uncapFirst"),

    /**
     * Converts an underscore into a CamelCase word
     *
     * @return the CamelCase version of the word
     */
    UNDERSCORE_TO_CAMEL_CASE("underscoreToCamelCase"),

    ;

    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }
}
