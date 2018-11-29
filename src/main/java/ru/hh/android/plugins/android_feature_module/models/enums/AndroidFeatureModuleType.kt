package ru.hh.android.plugins.android_feature_module.models.enums


enum class AndroidFeatureModuleType(
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