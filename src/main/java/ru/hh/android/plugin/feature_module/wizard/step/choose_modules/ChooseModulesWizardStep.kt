package ru.hh.android.plugin.feature_module.wizard.step.choose_modules

import ru.hh.android.plugin.feature_module.core.BaseWizardStep
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel
import ru.hh.android.plugins.android_feature_module.models.ModuleListItem
import ru.hh.android.plugins.android_feature_module.wizard.uikit.CheckboxesListView
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JTextPane


class ChooseModulesWizardStep(
        override val presenter: ChooseModulesPresenter
) : BaseWizardStep<PluginWizardModel, ChooseModulesView>(), ChooseModulesView {

    override lateinit var contentPanel: JPanel

    private lateinit var librariesList: JList<*>
    private lateinit var libraryDescriptionArea: JTextPane
    private lateinit var enableAllButton: JButton
    private lateinit var disableAllButton: JButton


    private fun createUIComponents() {
        librariesList = CheckboxesListView<ModuleListItem> {

        }
    }

}