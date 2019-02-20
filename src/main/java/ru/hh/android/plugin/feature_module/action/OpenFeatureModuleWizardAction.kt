package ru.hh.android.plugin.feature_module.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.feature_module._test.TestComponent
import ru.hh.android.plugin.feature_module.extensions.runWriteAction
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.MainParametersHolder
import ru.hh.android.plugin.feature_module.wizard.PluginWizardDialog
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel


class OpenFeatureModuleWizardAction : AnAction() {

    override fun actionPerformed(actionEvent: AnActionEvent) {
        actionEvent.project?.let { project ->
            val config = CreateModuleConfig(
                    mainParams = MainParametersHolder(),
                    libraries = emptyList(),
                    applications = emptyList()
            )

            PluginWizardDialog(PluginWizardModel(project, config)) { model ->
                project.runWriteAction {
                    val taskConfig = model.getTaskConfig()

//                    val buildModuleComponent = project.getComponent(BuildModuleComponent::class.java)
//                    buildModuleComponent.buildNewFeatureModule(taskConfig)
                    val testComponent = project.getComponent(TestComponent::class.java)
                    testComponent.create(taskConfig)
                }
            }.show()

        }
    }

}