package ru.hh.android.plugin.core.wizard

import com.intellij.ui.wizard.WizardModel


abstract class WizardStepPresenter<WM, WV> where WM : WizardModel, WV : WizardStepView {

    protected lateinit var view: WV


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