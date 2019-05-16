package ru.hh.android.plugin.wizard.step.choose_applications

import ru.hh.android.plugin.core.BaseView
import ru.hh.android.plugin.wizard.step.choose_applications.model.AppModuleDisplayableItem


interface ChooseApplicationsView : BaseView {

    fun showList(items: List<AppModuleDisplayableItem>)

    fun repaintList()

}