package ru.hh.plugins

import org.gradle.api.JavaVersion
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class ExternalLibrariesExtension @Inject constructor(private val providers: ProviderFactory) {

    val javaVersion = JavaVersion.VERSION_17
    val chosenIdeaVersion: Product = Product.LocalIde(
        pathToIde = systemProperty("androidStudioPath").get(),
        compilerVersion = systemProperty("androidStudioCompilerVersion").get(),
        pluginsNames = systemProperty("androidStudioPluginsNames").get()
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
    )

    private val gradleIntellijPluginVersion = systemProperty("gradleIntellijPluginVersion").get()
    private val gradleChangelogPluginVersion = systemProperty("gradleChangelogPluginVersion").get()
    private val kotlinVersion = systemProperty("kotlinVersion").get()
    private val detektVersion = systemProperty("detektVersion").get()

    val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    val gradleIntelliJPlugin = "org.jetbrains.intellij.plugins:gradle-intellij-plugin:$gradleIntellijPluginVersion"
    val gradleChangelogPlugin = "org.jetbrains.intellij.plugins:gradle-changelog-plugin:$gradleChangelogPluginVersion"

    val kotlinXCli = "org.jetbrains.kotlinx:kotlinx-cli:0.2.1"
    val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    val kotlinStdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
    val kotlinStdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
    val kotlinHtml = "org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2"
    val kotlinCompilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion"

    val freemarker = "org.freemarker:freemarker:2.3.30"
    val flexmark = "com.vladsch.flexmark:flexmark-all:0.50.42"

    val staticAnalysis = StaticAnalysisLibraries(
        detektVersion = detektVersion
    )
    val tests = UnitTests

    object UnitTests {
        const val kotest = "io.kotest:kotest-runner-junit5-jvm:4.3.1"
    }

    class StaticAnalysisLibraries(
        detektVersion: String
    ) {
        val detektFormatting = "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion"
        val detektCli = "io.gitlab.arturbosch.detekt:detekt-cli:$detektVersion"
        val detektApi = "io.gitlab.arturbosch.detekt:detekt-api:$detektVersion"
        val detektTest = "io.gitlab.arturbosch.detekt:detekt-test:$detektVersion"
        val detektGradlePlugin = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektVersion"
    }

    sealed class Product {

        abstract val pluginsNames: List<String>

        data class LocalIde(
            override val pluginsNames: List<String>,
            val pathToIde: String,
            // For the local version of Android Studio,
            // you must specify the compiler version for IntelliJInstrumentCodeTask (look into `About` screen)
            val compilerVersion: String
        ) : Product()

        data class ICBasedIde(
            override val pluginsNames: List<String>,
            val ideVersion: String
        ) : Product()
    }

    enum class PredefinedIdeProducts(val product: Product.ICBasedIde) {
        ANDROID_STUDIO_ARCTIC_FOX(
            Product.ICBasedIde(
                ideVersion = "202.7660.26",
                pluginsNames = listOf(
                    "android",
                    "Kotlin",
                    "java",
                    "Groovy",
                    "git4idea",
                    "IntelliLang"
                )
            )
        ),

        ANDROID_STUDIO_4_2(
            Product.ICBasedIde(
                ideVersion = "202.7660.26",
                pluginsNames = listOf(
                    "android",
                    "Kotlin",
                    "java",
                    "Groovy",
                    "git4idea",
                    "IntelliLang"
                )
            )
        ),

        IDEA_2020_2(
            Product.ICBasedIde(
                ideVersion = "2020.2",
                pluginsNames = listOf(
                    "android",
                    "Kotlin",
                    "java",
                    "Groovy",
                    "git4idea"
                )
            )
        ),

        ANDROID_STUDIO_4_1(
            Product.ICBasedIde(
                ideVersion = "201.8743.12",
                pluginsNames = listOf(
                    "android",
                    "Kotlin",
                    "java",
                    "Groovy",
                    "git4idea"
                )
            )
        ),

        ANDROID_STUDIO_4_0(
            Product.ICBasedIde(
                ideVersion = "193.6911.18",
                pluginsNames = listOf(
                    "android",
                    "Kotlin",
                    "java",
                    "Groovy",
                    "git4idea"
                )
            )
        ),

        ANDROID_STUDIO_3_6_3(
            Product.ICBasedIde(
                ideVersion = "192.7142.36",
                pluginsNames = listOf(
                    "android",
                    "Kotlin",
                    "java",
                    "Groovy",
                    "git4idea"
                )
            )
        ),

        ANDROID_STUDIO_3_5_3(
            Product.ICBasedIde(
                ideVersion = "191.8026.42",
                pluginsNames = listOf(
                    "android",
                    "Kotlin",
                    "Groovy",
                    "git4idea"
                )
            )
        )
    }

    private fun systemProperty(name: String): Provider<String> {
        return providers.systemProperty(name)
    }
}
