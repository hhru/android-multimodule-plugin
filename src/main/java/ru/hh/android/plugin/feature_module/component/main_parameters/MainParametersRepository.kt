package ru.hh.android.plugin.feature_module.component.main_parameters

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.feature_module.model.MainParametersHolder

class MainParametersRepository : ProjectComponent {

    var currentMainParametersHolder: MainParametersHolder? = null



}