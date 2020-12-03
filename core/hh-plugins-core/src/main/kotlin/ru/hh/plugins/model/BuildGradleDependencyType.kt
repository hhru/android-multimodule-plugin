package ru.hh.plugins.model


/**
 * Type on dependency declaration
 */
enum class BuildGradleDependencyType(
    val yamlKey: String
) {
    COMPILE_ONLY("compileOnly"),
    IMPLEMENTATION("implementation"),
    API("api");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }

}