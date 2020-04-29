package ru.hh.android.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.extensions.runWriteAction
import ru.hh.android.plugin.generator.FeatureModuleGenerator
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardDialog


class OpenFeatureModuleWizardAction : AnAction() {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        actionEvent.project?.let { project ->

            FeatureModuleWizardDialog(project) { model ->
                project.runWriteAction {
                    val taskConfig = CreateModuleConfig(
                            params = model.params,
                            libraries = model.selectedModules.toList(),
                            applications = model.selectedApps.toList()
                    )

                    val featureModuleGenerator = project.getComponent(FeatureModuleGenerator::class.java)
                    featureModuleGenerator.create(taskConfig)
                }
            }.show()
        }
    }

}