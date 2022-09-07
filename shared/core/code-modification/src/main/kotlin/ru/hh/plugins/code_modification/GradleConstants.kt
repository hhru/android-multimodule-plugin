package ru.hh.plugins.code_modification

internal object GradleConstants {

    const val GROOVY_EXTENSION = "gradle"
    const val KTS_EXTENSION = "kts"

    const val BUILD = "build"
    const val SETTINGS = "settings"

    const val BUILD_GRADLE_FILENAME = "$BUILD.$GROOVY_EXTENSION"
    const val BUILD_GRADLE_KTS_FILENAME = "$BUILD_GRADLE_FILENAME.$KTS_EXTENSION"

    const val SETTINGS_GRADLE_FILENAME = "$SETTINGS.$GROOVY_EXTENSION"
    const val SETTINGS_GRADLE_KTS_FILENAME = "$SETTINGS_GRADLE_FILENAME.$KTS_EXTENSION"
}
