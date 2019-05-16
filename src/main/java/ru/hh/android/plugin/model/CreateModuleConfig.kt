package ru.hh.android.plugin.model

import ru.hh.android.plugin.wizard.step.choose_applications.model.AppModuleDisplayableItem
import ru.hh.android.plugin.wizard.step.choose_modules.model.LibraryModuleDisplayableItem


data class CreateModuleConfig(
        var mainParams: MainParametersHolder,
        var libraries: List<LibraryModuleDisplayableItem>,
        var applications: List<AppModuleDisplayableItem>
)