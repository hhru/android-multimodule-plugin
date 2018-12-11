package ru.hh.android.plugin.feature_module.wizard

import com.intellij.openapi.project.Project
import com.intellij.ui.wizard.WizardDialog

class PluginWizardDialog(
        model: PluginWizardModel,
        private val project: Project,
        private val goalAchievedListener: PluginWizardDialog.GoalAchievedListener
) : WizardDialog<PluginWizardModel>(true, true, model) {

    override fun onWizardGoalAchieved() {
        super.onWizardGoalAchieved()
        goalAchievedListener.onGoalAchieved()
    }


    interface GoalAchievedListener {

        fun onGoalAchieved()

    }

}