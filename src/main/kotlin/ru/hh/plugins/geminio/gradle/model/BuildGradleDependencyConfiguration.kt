package ru.hh.plugins.geminio.gradle.model

/**
 * Dependency declaration configuration.
 */
enum class BuildGradleDependencyConfiguration(
    val yamlKey: String
) {
    KAPT("kapt"),
    KSP("ksp"),
    COMPILE_ONLY("compileOnly"),
    IMPLEMENTATION("implementation"),
    API("api"),
    TEST_IMPLEMENTATION("testImplementation"),
    ANDROID_TEST_IMPLEMENTATION("androidTestImplementation");

    companion object {
        fun fromYamlKey(yamlKey: String) = entries.firstOrNull { it.yamlKey == yamlKey }
        fun availableYamlKeys() = entries.joinToString { "'${it.yamlKey}'" }
    }
}
