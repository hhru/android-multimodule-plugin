object Versions {
    const val kotlin = "1.4.10"
    const val intellijPlugin = "0.6.2"

    const val junitVersion = "5.6.0"

    val chosenProduct = Product.LOCAL
}

object BuildPlugins {
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object Libs {
    const val junitJupiterApi = "org.junit.jupiter:junit-jupiter-api:${Versions.junitVersion}"
    const val junitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junitVersion}"
    const val junitJupiterParams = "org.junit.jupiter:junit-jupiter-params:${Versions.junitVersion}"
}

enum class Product(
    val isLocal: Boolean = false,
    val ideVersion: String,
    val pluginsNames: List<String>
) {
    LOCAL(
        isLocal = true,
        ideVersion = "/Applications/Android Studio 4.0.app",
        pluginsNames = listOf(
            "android",
            "android-layoutlib",
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