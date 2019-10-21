package ru.hh.android.plugin.core.wizard

import com.intellij.ui.wizard.WizardModel


abstract class WizardStepPresenter<WM : WizardModel, WV : WizardStepView, FS : WizardStepFormState> {

    protected lateinit var view: WV


    abstract fun validateForm(formState: FS): Boolean

    abstract fun updateModel(model: WM, formState: FS)


    fun bindView(view: WV) {
        this.view = view
    }

    open fun onNextButtonClicked(model: WM) {
        // do nothing by default.
    }

    open fun onPreviousButtonClicked(model: WM) {
        // do nothing by default.
    }

    open fun onCreate(model: WM) {
        // do nothing by default.
    }

    open fun onDestroy() {
        // do nothing by default.
    }

}