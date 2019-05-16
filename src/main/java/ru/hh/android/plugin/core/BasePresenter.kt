package ru.hh.android.plugin.core

import com.intellij.ui.wizard.WizardModel


abstract class BasePresenter<WM : WizardModel, T : BaseView> {

    protected lateinit var view: T


    fun bindView(view: T) {
        this.view = view
    }


    open fun onNextButtonClicked(model: WM) {
        // nothing to do by default.
    }

    open fun onPreviousButtonClicked(model: WM) {
        // nothing to do by default.
    }

    open fun onCreate(model: WM) {
        // nothing to do by default.
    }

    open fun onDestroy() {
        // nothing to do by default.
    }

}