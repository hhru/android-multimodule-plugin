package ru.hh.android.plugin.feature_module.wizard.step.choose_applications

import ru.hh.android.plugin.feature_module.core.BaseView
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.model.AppModuleDisplayableItem


interface ChooseApplicationsView : BaseView {

    fun showList(items: List<AppModuleDisplayableItem>)

    fun repaintList()

}