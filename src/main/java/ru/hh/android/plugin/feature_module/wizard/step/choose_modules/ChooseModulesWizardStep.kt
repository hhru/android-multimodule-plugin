package ru.hh.android.plugin.feature_module.wizard.step.choose_modules

import ru.hh.android.plugin.feature_module.core.BaseWizardStep
import ru.hh.android.plugin.feature_module.core.ui.custom_view.CheckBoxListView
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model.LibraryModuleDisplayableItem
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JTextPane


class ChooseModulesWizardStep(
        override val model: PluginWizardModel,
        override val presenter: ChooseModulesPresenter
) : BaseWizardStep<PluginWizardModel, ChooseModulesView>(), ChooseModulesView {

    override lateinit var contentPanel: JPanel

    private lateinit var librariesList: JList<LibraryModuleDisplayableItem>
    private lateinit var libraryDescriptionArea: JTextPane
    private lateinit var enableAllButton: JButton
    private lateinit var disableAllButton: JButton


    override fun onCreate() {
        super.onCreate()

        enableAllButton.addActionListener { presenter.onEnableAllButtonClicked() }
        disableAllButton.addActionListener { presenter.onDisableAllButtonClicked() }
    }


    override fun showList(items: List<LibraryModuleDisplayableItem>) {
        (librariesList as CheckBoxListView<LibraryModuleDisplayableItem>).setItems(items)
    }

    override fun repaintList() {
        librariesList.repaint()
    }


    private fun createUIComponents() {
        librariesList = CheckBoxListView(
                onItemSelectedListener = { presenter.onLibraryItemSelected(it) },
                onItemToggleChangedListener = { presenter.onLibraryItemToggleChanged(it) }
        )
    }

}