package ru.hh.android.plugin.feature_module.model

import ru.hh.android.plugin.feature_module.extensions.replaceWordsBreakers
import ru.hh.android.plugin.feature_module.model.enums.FeatureModuleType


data class MainParametersHolder(
        val moduleName: String = "",
        val packageName: String = "",
        val moduleType: FeatureModuleType = FeatureModuleType.STANDALONE,
        val enableMoxy: Boolean = false,
        val addUIModulesDependencies: Boolean = false,
        val needCreateAPIInterface: Boolean = false,
        val needCreateRepositoryWithInteractor: Boolean = false,
        val needCreateInterfaceForRepository: Boolean = false,
        val needCreatePresentationLayer: Boolean = false
) {

    val formattedLibraryName: String
        get() {
            return with(StringBuilder()) {
                moduleName
                        .replaceWordsBreakers()
                        .split(' ')
                        .map { it.capitalize() }
                        .forEach { append(it) }
                toString()
            }
        }

    val layoutName: String
        get() {
            return formattedLibraryName.toLowerCase()
        }

}