package ru.hh.plugins.geminio.sdk.recipe.models.predefined

enum class PredefinedFeature(
    val yamlKey: String
) {
    /**
     * Adds two additional string parameters for modules creation:
     *
     * - __moduleName - for module's name.
     * - __formattedModuleName - for formatted module's name (my-module -> MyModule).
     * - __packageName - for hardcoded parameters: ${packageName} + ${applicationPackage}.
     *
     * This feature is required for modules templates.
     */
    ENABLE_MODULE_CREATION_PARAMS("enableModuleCreationParams"),

    ;

    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }
}
