package ru.hh.plugins.geminio.model.enums


enum class GeminioRecipeExpressionModifier(
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
     * Converts an underlined_word into a CamelCase word
     * ... there is one more function in Android Studio helpers -> 'underscoreToCamelCase' but it will do the same.
     *
     * @return the CamelCase version of the word
     */
    UNDERLINES_TO_CAMEL_CASE("underlinesToCamelCase"),

    ;


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
    }
}