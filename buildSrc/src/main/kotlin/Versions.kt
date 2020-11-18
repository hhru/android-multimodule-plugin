object Versions {
    const val kotlin = "1.4.10"
    const val intellijPlugin = "0.6.3"

    val chosenProduct = Product.LOCAL
}

object BuildPlugins {
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Libs {
    const val freemarker = "org.freemarker:freemarker:2.3.30"
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