package ru.hh.android.plugin.feature_module.model

import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.model.AppModuleDisplayableItem
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model.LibraryModuleDisplayableItem


data class CreateModuleConfig(
        var mainParams: MainParametersHolder,
        var libraries: List<LibraryModuleDisplayableItem>,
        var applications: List<AppModuleDisplayableItem>
)