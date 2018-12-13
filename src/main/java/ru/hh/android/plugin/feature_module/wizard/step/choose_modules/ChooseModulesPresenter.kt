package ru.hh.android.plugin.feature_module.wizard.step.choose_modules

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.feature_module.component.main_parameters.MainParametersInteractor
import ru.hh.android.plugin.feature_module.component.module.ModuleInteractor
import ru.hh.android.plugin.feature_module.core.BasePresenter

class ChooseModulesPresenter(
        private val moduleInteractor: ModuleInteractor,
        private val mainParametersInteractor: MainParametersInteractor
) : BasePresenter<ChooseModulesView>(), ProjectComponent {

    override fun onCreate() {
        super.onCreate()

        moduleInteractor.getLibrariesModules()
    }


}