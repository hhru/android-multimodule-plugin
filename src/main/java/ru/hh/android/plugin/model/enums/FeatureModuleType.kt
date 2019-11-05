package ru.hh.android.plugin.model.enums

import ru.hh.android.plugin.extensions.EMPTY


enum class FeatureModuleType(
        val radioButtonText: String,
        val folderPrefix: String
) {

    STANDALONE(
            radioButtonText = "Standalone",
            folderPrefix = ""
    ),

    FEATURE_MODULE(
            radioButtonText = "Feature module",
            folderPrefix = "feature"
    ),

    CORE_MODULE(
            radioButtonText = "Core module",
            folderPrefix = "core"
    ),

    CUSTOM_PATH(
            radioButtonText = "Custom path",
            folderPrefix = String.EMPTY
    )

}