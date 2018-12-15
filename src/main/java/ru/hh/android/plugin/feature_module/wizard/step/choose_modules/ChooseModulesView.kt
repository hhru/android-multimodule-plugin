package ru.hh.android.plugin.feature_module.wizard.step.choose_modules

import ru.hh.android.plugin.feature_module.core.BaseView
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model.LibraryModuleDisplayableItem

interface ChooseModulesView : BaseView {

    fun showList(displayableItems: List<LibraryModuleDisplayableItem>)

    fun repaintList()

}