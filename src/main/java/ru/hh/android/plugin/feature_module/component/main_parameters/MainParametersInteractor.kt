package ru.hh.android.plugin.feature_module.component.main_parameters

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.feature_module.model.MainParametersHolder


class MainParametersInteractor(
        private val mainParametersRepository: MainParametersRepository
) : ProjectComponent {

    fun getForceEnabledModulesNamesForParameters(): Set<String> {
        return mainParametersRepository.currentMainParametersHolder?.let { parameters ->
            mutableSetOf<String>().apply {
                this += "common"
                this += "logger"
                this += "analytics"
                this += "core-utils"

                if (parameters.addUIModulesDependencies) {
                    this += "base-ui"
                }

                if (parameters.needCreateAPIInterface) {
                    this += "network-source"
                    this += "network-auth-source"
                }
            }
        } ?: emptySet()
    }

    fun saveMainParameters(mainParametersHolder: MainParametersHolder) {
        mainParametersRepository.currentMainParametersHolder = mainParametersHolder
    }

    fun clearMainParameters() {
        mainParametersRepository.currentMainParametersHolder = null
    }

}