package ru.hh.android.core

import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import javax.swing.JComponent
import javax.swing.JPanel


abstract class BaseWizardStep<WM : WizardModel, BV : BaseView> : WizardStep<WM>() {

    abstract val contentPanel: JPanel
    abstract val presenter: BasePresenter<BV>


    @Suppress("UNCHECKED_CAST")
    override fun prepare(state: WizardNavigationState?): JComponent {
        presenter.bindView(this as BV)
        presenter.onCreate()
        onCreate()
        return contentPanel
    }

    override fun onCancel(): Boolean {
        presenter.onDestroy()
        return super.onCancel()
    }


    open fun onCreate() {
        // nothing to do by default.
    }

    open fun onDestroy() {
        // nothing to do by default.
    }

}