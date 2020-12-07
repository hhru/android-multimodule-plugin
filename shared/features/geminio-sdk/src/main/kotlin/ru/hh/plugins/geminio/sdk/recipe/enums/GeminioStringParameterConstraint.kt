package ru.hh.plugins.geminio.sdk.recipe.enums


/**
 * Constraints that can be applied to a parameter which helps the UI add a validator etc for user input.
 * These are typically combined into a set of constraints via an EnumSet.
 */
enum class GeminioStringParameterConstraint(
    val yamlKey: String
) {

    /**
     * This value must be unique. This constraint usually only makes sense when other constraints are specified, such as [LAYOUT],
     * which means that the parameter should designate a name that does not represent an existing layout resource name.
     */
    UNIQUE("unique"),

    /**
     * This value must already exist. This constraint usually only makes sense when other constraints are specified, such as [LAYOUT],
     * which means that the parameter should designate a name that already exists as a resource name.
     */
    EXISTS("exists"),

    /** The associated value must not be empty. */
    NONEMPTY("nonempty"),

    /** The associated value should represent a fully qualified activity class name. */
    ACTIVITY("activity"),

    /** The associated value should represent a valid class name. */
    CLASS("class"),

    /** The associated value should represent a valid package name. */
    PACKAGE("package"),

    /** The associated value should represent a valid Android application package name. */
    APP_PACKAGE("app_package"),

    /** The associated value should represent a valid Module name. */
    MODULE("module"),

    /** The associated value should represent a valid layout resource name. */
    LAYOUT("layout"),

    /** The associated value should represent a valid drawable resource name. */
    DRAWABLE("drawable"),

    /** The associated value should represent a valid navigation resource name. */
    NAVIGATION("navigation"),

    /** The associated value should represent a valid values file name. */
    VALUES("values"),

    /** The associated value should represent a valid source directory name. */
    SOURCE_SET_FOLDER("source_set_folder"),

    /** The associated value should represent a valid string resource name. */
    STRING("string"),

    /**  The associated value should represent a valid URI authority. Format: [userinfo@]host[:port] */
    URI_AUTHORITY("uri_authority"),

    /** The associated value should represent a package-level Kotlin function. */
    KOTLIN_FUNCTION("kotlin_function");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }
}