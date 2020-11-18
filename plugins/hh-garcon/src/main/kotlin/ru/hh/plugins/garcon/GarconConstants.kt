package ru.hh.plugins.garcon


object GarconConstants {

    const val DEFAULT_GARCON_NOTIFICATIONS_TITLE = "Garcon"

    const val AGODA_SCREEN_CLASS_FQN = "com.agoda.kakao.screen.Screen"

    const val HH_SCREEN_INTENTIONS_CLASS_FQN = "ru.hh.android.core_tests.page.ScreenIntentions"

    const val DEFAULT_PACKAGE_NAME = "com.example.myapplication"


    object RecentsKeys {
        private const val BASE = "ru.hh.android.plugins.garcon"

        const val OPEN_IN_EDITOR_FLAG = "$BASE.open_in_editor"
        const val TARGET_PACKAGE_NAME = "$BASE.target_package_name"
        const val TARGET_SCREEN_CLASS = "$BASE.target_screen_class"
    }

}

