package ru.hh.android.plugin.feature_module.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.feature_module.extensions.runWriteAction
import ru.hh.android.plugin.feature_module.generator.FeatureModuleGenerator
import ru.hh.android.plugin.feature_module.wizard.PluginWizardDialog
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel


class OpenFeatureModuleWizardAction : AnAction() {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        actionEvent.project?.let { project ->

            PluginWizardDialog(PluginWizardModel(project)) { model ->
                project.runWriteAction {
                    val taskConfig = model.getTaskConfig()

                    val featureModuleGenerator = project.getComponent(FeatureModuleGenerator::class.java)
                    featureModuleGenerator.create(taskConfig)
                }
            }.show()

        }
    }

}