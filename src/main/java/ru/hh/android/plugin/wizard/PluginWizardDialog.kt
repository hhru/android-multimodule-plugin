package ru.hh.android.plugin.wizard

import com.intellij.ui.wizard.WizardDialog
import com.intellij.util.ui.JBUI
import java.awt.Dimension


class PluginWizardDialog(
        model: PluginWizardModel,
        private val goalAchievedListener: (PluginWizardModel) -> Unit
) : WizardDialog<PluginWizardModel>(true, true, model) {

    companion object {
        private const val PREFERRED_DIALOG_WIDTH = 800
        private const val PREFERRED_DIALOG_HEIGHT = 450
    }


    override fun onWizardGoalAchieved() {
        super.onWizardGoalAchieved()
        goalAchievedListener.invoke(myModel)
    }

    override fun getPreferredSize(): Dimension {
        return JBUI.size(PREFERRED_DIALOG_WIDTH, PREFERRED_DIALOG_HEIGHT)
    }

}