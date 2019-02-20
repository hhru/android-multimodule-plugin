package ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters

import ru.hh.android.plugin.feature_module.core.BaseView

interface ChooseMainParametersView : BaseView {

    fun changeModuleName(newModuleName: String)

    fun changePackageName(newPackageName: String)

    fun changePackageNameLabel(newPackageName: String)

    fun showEditPackageNameLabel(needShow: Boolean)

    fun showEditPackageNameTextField(needShow: Boolean)

    fun changeEditPackageNameButtonText(buttonText: String)

    fun showError(errorText: String)

}