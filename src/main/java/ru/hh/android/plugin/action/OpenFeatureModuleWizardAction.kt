package ru.hh.android.plugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.extensions.runWriteAction
import ru.hh.android.plugin.generator.FeatureModuleGenerator
import ru.hh.android.plugin.wizard.PluginWizardDialog
import ru.hh.android.plugin.wizard.PluginWizardModel


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