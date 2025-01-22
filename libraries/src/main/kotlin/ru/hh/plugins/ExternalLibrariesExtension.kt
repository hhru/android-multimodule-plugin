package ru.hh.plugins

import org.gradle.api.JavaVersion
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class ExternalLibrariesExtension @Inject constructor(private val providers: ProviderFactory) {

    val javaVersion = JavaVersion.VERSION_21
    val chosenIdeaVersion: Product = Product.LocalIde(
        pathToIde = systemProperty("androidStudioPath").get(),
    )

    private val androidStudioPluginVersion = systemProperty("androidStudioPluginVersion").get()
    private val gradleIntellijPluginVersion = systemProperty("gradleIntellijPluginVersion").get()
    private val gradleChangelogPluginVersion = systemProperty("gradleChangelogPluginVersion").get()
    private val kotlinVersion = systemProperty("kotlinVersion").get()
    private val detektVersion = systemProperty("detektVersion").get()

    val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    val gradleIntelliJPlugin =
        "org.jetbrains.intellij.platform:org.jetbrains.intellij.platform.gradle.plugin:$gradleIntellijPluginVersion"
    val gradleChangelogPlugin = "org.jetbrains.intellij.plugins:gradle-changelog-plugin:$gradleChangelogPluginVersion"

    val androidStudioPlugin = "org.jetbrains.android:$androidStudioPluginVersion"
    val kotlinXCli = "org.jetbrains.kotlinx:kotlinx-cli:0.2.1"
    val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    val kotlinStdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
    val kotlinStdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
    val kotlinHtml = "org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3"
    val kotlinCompilerEmbeddable = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion"

    val freemarker = "org.freemarker:freemarker:2.3.30"
    val flexmark = "com.vladsch.flexmark:flexmark-all:0.50.42"

    val staticAnalysis = StaticAnalysisLibraries(
        detektVersion = detektVersion
    )
    val tests = UnitTests

    object UnitTests {
        const val kotest = "io.kotest:kotest-runner-junit5-jvm:4.3.1"
        const val junit4 = "junit:junit:4.13.2"
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
        data class LocalIde(
            val pathToIde: String,
        ) : Product()

        data class ICBasedIde(
            val ideVersion: String
        ) : Product()
    }

    @Suppress("detekt.StringLiteralDuplication")
    enum class PredefinedIdeProducts(val product: Product) {
        ANDROID_STUDIO_LADYBUG_FEATURE_DROP(
            Product.ICBasedIde(
                ideVersion = "242.23726.1",
            )
        ),
        ANDROID_STUDIO_LADYBUG(
            Product.ICBasedIde(
                ideVersion = "242.23339.11",
            )
        ),
    }

    private fun systemProperty(name: String): Provider<String> {
        return providers.systemProperty(name)
    }
}
