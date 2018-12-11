package ru.hh.android.plugin.feature_module.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.feature_module.wizard.PluginWizardDialog
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel

class OpenFeatureModuleWizardAction : AnAction(), PluginWizardDialog.GoalAchievedListener {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            PluginWizardDialog(PluginWizardModel(project), project, this).show()
        }
    }


    override fun onGoalAchieved() {

    }

}