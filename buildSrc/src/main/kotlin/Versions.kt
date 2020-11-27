object Versions {
    const val kotlin = "1.4.20"
    const val intellijPlugin = "0.6.5"

    val chosenProduct = Product.LOCAL
}

object GradlePlugins {
    const val gradleIntelliJPlugin = "org.jetbrains.intellij"

    const val setupIdeaPlugin = "ru.hh.plugins.gradle.setup_idea_plugin"
}

object BuildPlugins {
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Libs {
    const val freemarker = "org.freemarker:freemarker:2.3.30"

    val tests = UnitTests

    object UnitTests {
        private const val kotestVersion = "4.3.1"

        const val kotest = "io.kotest:kotest-runner-junit5:$kotestVersion"
    }
}


enum class Product(
    val isLocal: Boolean = false,
    val ideVersion: String,
    val pluginsNames: List<String>
) {
    LOCAL(
        isLocal = true,
        ideVersion = "/Applications/Android Studio.app",
        pluginsNames = listOf(
            "android",
            "android-layoutlib",
            "Kotlin",
            "java",
            "Groovy",
            "git4idea",
            "IntelliLang"
        )
    ),

    IDEA_2020_2(
        isLocal = false,
        ideVersion = "2020.2",
        pluginsNames = listOf(
            "android",
            "Kotlin",
            "java",
            "Groovy",
            "git4idea"
        )
    ),

    ANDROID_STUDIO_4_1(
        ideVersion = "201.8743.12",
        pluginsNames = listOf(
            "android",
            "Kotlin",
            "java",
            "Groovy",
            "git4idea"
        )
    ),

    ANDROID_STUDIO_4_0(
        ideVersion = "193.6911.18",
        pluginsNames = listOf(
            "android",
            "Kotlin",
            "java",
            "Groovy",
            "git4idea"
        )
    ),

    ANDROID_STUDIO_3_6_3(
        ideVersion = "192.7142.36",
        pluginsNames = listOf(
            "android",
            "Kotlin",
            "java",
            "Groovy",
            "git4idea"
        )
    ),

    ANDROID_STUDIO_3_5_3(
        ideVersion = "191.8026.42",
        pluginsNames = listOf(
            "android",
            "Kotlin",
            "Groovy",
            "git4idea"
        )
    )
}