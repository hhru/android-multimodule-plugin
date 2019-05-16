package ru.hh.android.plugin.component.main_parameters

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.model.MainParametersHolder


class MainParametersInteractor : ProjectComponent {

    fun getForceEnabledModulesNamesForParameters(
            mainParametersHolder: MainParametersHolder?
    ): Set<String> {
        return mainParametersHolder?.let { parameters ->
            mutableSetOf<String>().apply {
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