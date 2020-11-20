package ru.hh.plugins.geminio.model


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

    ESCAPE_KOTLIN_IDENTIFIER("escapeKotlinIdentifier"),

    /**
     * Creates a Java class name out of the given string, if possible.
     * For example, "My Project" becomes "MyProject", "hello" becomes "Hello", "Java's" becomes "Javas", and so on.
     *
     * @return the string as a Java class, or null if a class name could not be extracted
     */
    EXTRACT_CLASS_NAME("extractClassName"),

    EXTRACT_LETTERS("extractLetters"),

    /**
     * Converts a String to Camel Case. The return will not contain any two consecutive upper case characters
     * For example:
     *
     * MyCLASsName to MyClassName
     * my_class_name to MyClassName
     * URL to Url
     **/
    TO_UPPER_CAMEL_CASE("toUpperCamelCase"),

    LAYOUT_TO_ACTIVITY("layoutToActivity"),

    LAYOUT_TO_FRAGMENT("layoutToFragment"),

    UNDERLINES_TO_CAMEL_CASE("underlinesToCamelCase"),

    UNDERSCORE_TO_CAMEL_CASE("underscoreToCamelCase"),

    ESCAPE_XML_ATTRIBUTE("escapeXmlAttribute");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
    }
}