package ru.hh.android.plugins.android_feature_module.models

import ru.hh.android.plugins.android_feature_module.models.enums.AndroidFeatureModuleType


data class MainNewModuleParameters @JvmOverloads constructor(
        var libraryName: String = "",
        var moduleName: String = "",
        var packageName: String = "",
        var moduleType: AndroidFeatureModuleType = AndroidFeatureModuleType.STANDALONE,
        var enableMoxy: Boolean = false,
        var addUIModuleDependencies: Boolean = false,
        var needCreateAPIInterface: Boolean = false,
        var needCreateRepositoryWithInteractor: Boolean = false
) {

    fun libraryName(libraryName: String): MainNewModuleParameters {
        this.libraryName = libraryName
        return this
    }

    fun moduleName(moduleName: String): MainNewModuleParameters {
        this.moduleName = moduleName
        return this
    }

    fun packageName(packageName: String): MainNewModuleParameters {
        this.packageName = packageName
        return this
    }

    fun moduleType(androidFeatureModuleType: AndroidFeatureModuleType): MainNewModuleParameters {
        this.moduleType = androidFeatureModuleType
        return this
    }

    fun enableMoxy(enableMoxy: Boolean): MainNewModuleParameters {
        this.enableMoxy = enableMoxy
        return this
    }

    fun addUIModuleDependencies(addUIModuleDependencies: Boolean): MainNewModuleParameters {
        this.addUIModuleDependencies = addUIModuleDependencies
        return this
    }

    fun needCreateAPIInterface(needCreateAPIInterface: Boolean): MainNewModuleParameters {
        this.needCreateAPIInterface = needCreateAPIInterface
        return this
    }

    fun needCreateRepositoryWithInteractor(needCreateRepositoryWithInteractor: Boolean): MainNewModuleParameters {
        this.needCreateRepositoryWithInteractor = needCreateRepositoryWithInteractor
        return this
    }

}