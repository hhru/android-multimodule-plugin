package ru.hh.plugins.geminio.sdk.recipe.enums


/**
 * Dependency declaration configuration.
 */
enum class BuildGradleDependencyConfiguration(
    val yamlKey: String
) {
    KAPT("kapt"),
    COMPILE_ONLY("compileOnly"),
    IMPLEMENTATION("implementation"),
    API("api"),
    TEST_IMPLEMENTATION("testImplementation"),
    ANDROID_TEST_IMPLEMENTATION("androidTestImplementation");


    companion object {
        fun fromYamlKey(yamlKey: String) = values().firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = values().joinToString { "'${it.yamlKey}'" }
    }

}