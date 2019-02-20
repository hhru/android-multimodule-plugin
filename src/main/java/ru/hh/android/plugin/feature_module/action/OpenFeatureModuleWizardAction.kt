package ru.hh.android.plugin.feature_module.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.feature_module.component.build_module.BuildModuleComponent
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.MainParametersHolder
import ru.hh.android.plugin.feature_module.wizard.PluginWizardDialog
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel


class OpenFeatureModuleWizardAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            val config = CreateModuleConfig(
                    mainParametersHolder = MainParametersHolder(),
                    libraries = emptyList(),
                    applications = emptyList()
            )

            PluginWizardDialog(PluginWizardModel(project, config), { model ->
                val taskConfig = model.getTaskConfig()

                val buildModuleComponent = project.getComponent(BuildModuleComponent::class.java)
                buildModuleComponent.buildNewFeatureModule(taskConfig)
            }).show()
        }
    }

}