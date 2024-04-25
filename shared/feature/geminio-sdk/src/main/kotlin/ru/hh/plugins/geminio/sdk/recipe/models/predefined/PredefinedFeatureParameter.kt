package ru.hh.plugins.geminio.sdk.recipe.models.predefined

sealed class PredefinedFeatureParameter {
    data class ModuleCreationParameter(
        val defaultPackageNamePrefix: String = DEFAULT_PACKAGE_NAME_PREFIX,
        val defaultSourceSet: String = DEFAULT_SOURCE_SET_NAME,
        val defaultSourceCodeFolderName: String = DEFAULT_SOURCE_CODE_FOLDER_NAME,
    ) : PredefinedFeatureParameter(){
        companion object {
            const val DEFAULT_PACKAGE_NAME_PREFIX = "ru.hh"
            const val DEFAULT_SOURCE_SET_NAME = "main"
            const val DEFAULT_SOURCE_CODE_FOLDER_NAME = "java"
        }
    }
}
