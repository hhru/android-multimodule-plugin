package ru.hh.android.plugin.feature_module.component.main_parameters

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.feature_module.model.MainParametersHolder


class MainParametersInteractor : ProjectComponent {

    fun getForceEnabledModulesNamesForParameters(
            mainParametersHolder: MainParametersHolder?
    ): Set<String> {
        return mainParametersHolder?.let { parameters ->
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

}