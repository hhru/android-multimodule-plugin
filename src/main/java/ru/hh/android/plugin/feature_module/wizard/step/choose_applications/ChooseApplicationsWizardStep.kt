package ru.hh.android.plugin.feature_module.wizard.step.choose_applications

import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.feature_module.core.BaseWizardStep
import ru.hh.android.plugin.feature_module.core.ui.custom_view.CheckBoxListView
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel
import ru.hh.android.plugin.feature_module.wizard.step.choose_applications.model.AppModuleDisplayableItem
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JTextPane


class ChooseApplicationsWizardStep(
        override val presenter: ChooseApplicationsPresenter
) : BaseWizardStep<PluginWizardModel, ChooseApplicationsView>(), ChooseApplicationsView {

    override lateinit var contentPanel: JPanel

    private lateinit var applicationsList: JList<AppModuleDisplayableItem>
    private lateinit var applicationDescriptionArea: JTextPane
    private lateinit var enableAllButton: JButton
    private lateinit var disableAllButton: JButton


    override fun onCreate() {
        super.onCreate()

        enableAllButton.addActionListener { presenter.onEnableAllButtonClicked() }
        disableAllButton.addActionListener { presenter.onDisableAllButtonClicked() }
    }

    override fun onNext(model: PluginWizardModel?): WizardStep<*> {
        val nextStep = super.onNext(model)
        presenter.onNextButtonClicked()

        return nextStep
    }

    override fun showList(items: List<AppModuleDisplayableItem>) {
        (applicationsList as CheckBoxListView<AppModuleDisplayableItem>).setItems(items)
    }

    override fun repaintList() {
        applicationsList.repaint()
    }

    private fun createUIComponents() {
        applicationsList = CheckBoxListView(
                onItemSelectedListener = { presenter.onAppModuleItemSelected(it) },
                onItemToggleChangedListener = { presenter.onAppModuleItemToggleChanged(it) }
        )
    }

}
