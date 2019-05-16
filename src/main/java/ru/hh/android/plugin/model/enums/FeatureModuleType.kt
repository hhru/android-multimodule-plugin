package ru.hh.android.plugin.model.enums


enum class FeatureModuleType(
        val typeRootFolder: String,
        private val comboBoxText: String
) {

    STANDALONE(
            comboBoxText = "Standalone",
            typeRootFolder = "/"
    ),

    FEATURE_MODULE(
            comboBoxText = "Feature module",
            typeRootFolder = "/feature"
    ),

    CORE_MODULE(
            comboBoxText = "Core module",
            typeRootFolder = "/core"
    );


    override fun toString(): String {
        return comboBoxText
    }

}