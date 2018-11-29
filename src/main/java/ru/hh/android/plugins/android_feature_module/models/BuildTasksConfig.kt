package ru.hh.android.plugins.android_feature_module.models


data class BuildTasksConfig(
        val libraryName: String,
        val moduleName: String,
        val packageName: String,
        val moduleType: AndroidFeatureModuleType,
        val enableMoxy: Boolean,
        val addUIModuleDependencies: Boolean,
        val needCreateAPIInterface: Boolean,
        val needCreateRepositoryWithInteractor: Boolean,
        val librariesModules: List<ModuleListItem>,
        val applicationsModules: List<ModuleListItem>
) {

    val slashedPackageName by lazy {
        packageName.replace('.', '/')
    }

    val formattedLibraryName by lazy {
        val words = libraryName.split(' ').map { it.capitalize() }

        val stringBuilder = StringBuilder()
        for (word in words) {
            stringBuilder.append(word)
        }
        stringBuilder.toString()
    }

}