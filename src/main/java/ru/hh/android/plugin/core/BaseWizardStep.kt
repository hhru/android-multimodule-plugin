package ru.hh.android.plugin.core

import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import javax.swing.JComponent
import javax.swing.JPanel


abstract class BaseWizardStep<WM : WizardModel, BV : BaseView> : WizardStep<WM>() {

    abstract val contentPanel: JPanel

    abstract val model: WM
    abstract val presenter: BasePresenter<WM, BV>


    @Suppress("UNCHECKED_CAST")
    override fun prepare(state: WizardNavigationState?): JComponent {
        presenter.bindView(this as BV)

        presenter.onCreate(model)
        onCreate()
        return contentPanel
    }

    override fun onCancel(): Boolean {
        presenter.onDestroy()
        return super.onCancel()
    }

    override fun onNext(model: WM): WizardStep<*> {
        presenter.onNextButtonClicked(model)
        return super.onNext(model)
    }

    override fun onPrevious(model: WM): WizardStep<*> {
        presenter.onPreviousButtonClicked(model)
        return super.onPrevious(model)
    }


    open fun onCreate() {
        // nothing to do by default.
    }

    open fun onDestroy() {
        // nothing to do by default.
    }

}