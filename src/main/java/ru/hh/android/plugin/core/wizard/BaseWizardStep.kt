package ru.hh.android.plugin.core.wizard

import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import javax.swing.JComponent


abstract class BaseWizardStep<WM, WV>(
        private val model: WM,
        private val presenter: WizardStepPresenter<WM, WV>
) : WizardStep<WM>() where WM : WizardModel, WV : WizardStepView {

    abstract fun getViewBuilder(): WizardStepViewBuilder


    @Suppress("UNCHECKED_CAST")
    override fun prepare(state: WizardNavigationState?): JComponent {
        presenter.bindView(this as WV)

        presenter.onCreate(model)
        onCreate()
        return getViewBuilder().build()
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