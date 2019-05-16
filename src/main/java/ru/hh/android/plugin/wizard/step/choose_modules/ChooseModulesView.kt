package ru.hh.android.plugin.wizard.step.choose_modules

import ru.hh.android.plugin.core.BaseView
import ru.hh.android.plugin.wizard.step.choose_modules.model.LibraryModuleDisplayableItem


interface ChooseModulesView : BaseView {

    fun showList(items: List<LibraryModuleDisplayableItem>)

    fun repaintList()

}