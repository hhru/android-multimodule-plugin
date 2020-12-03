package ru.hh.plugins.model


/**
 * Type on dependency declaration
 */
enum class BuildGradleDependencyType(
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