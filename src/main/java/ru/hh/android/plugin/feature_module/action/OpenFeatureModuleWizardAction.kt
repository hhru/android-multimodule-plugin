package ru.hh.android.plugin.feature_module.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.feature_module.model.CreateModuleTaskConfig
import ru.hh.android.plugin.feature_module.model.MainParametersHolder
import ru.hh.android.plugin.feature_module.wizard.PluginWizardDialog
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel


class OpenFeatureModuleWizardAction : AnAction(), PluginWizardDialog.GoalAchievedListener {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            val config = CreateModuleTaskConfig(
                    mainParametersHolder = MainParametersHolder(),
                    libraries = emptyList(),
                    applications = emptyList()
            )

            PluginWizardDialog(PluginWizardModel(project, config), this).show()
        }
    }


    override fun onGoalAchieved(model: PluginWizardModel) {
        val config = model.getTaskConfig()
        // todo
    }

}