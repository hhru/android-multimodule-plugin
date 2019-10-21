package ru.hh.android.plugin.core.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import javax.swing.JComponent


abstract class BaseWizardStep<
        WM : WizardModel,
        WV : WizardStepView,
        FS : WizardStepFormState,
        WVB : WizardStepViewBuilder,
        WP : WizardStepPresenter<WM, WV, FS>
        >(
        protected val model: WM,
        protected val project: Project
) : WizardStep<WM>() {

    abstract fun getPresenter(project: Project): WP

    abstract fun getViewBuilder(): WVB


    protected lateinit var uiBuilder: WVB
    protected lateinit var presenter: WP


    @Suppress("UNCHECKED_CAST")
    override fun prepare(state: WizardNavigationState?): JComponent {
        presenter = getPresenter(project)
        presenter.bindView(this as WV)
        presenter.onCreate(model)
        onCreate()

        uiBuilder = getViewBuilder()
        return uiBuilder.build()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onNext(model: WM): WizardStep<*>? {
        val formState = uiBuilder.collectFormState() as FS
        if (presenter.validateForm(formState)) {
            presenter.updateModel(model, formState)
            return super.onNext(model)
        }

        return null
    }

    override fun onCancel(): Boolean {
        presenter.onDestroy()
        return super.onCancel()
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