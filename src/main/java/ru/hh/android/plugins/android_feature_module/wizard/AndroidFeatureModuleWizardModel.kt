package ru.hh.android.plugins.android_feature_module.wizard

import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugins.android_feature_module.ProjectInfo
import ru.hh.android.plugins.android_feature_module.models.MainNewModuleParameters
import ru.hh.android.plugins.android_feature_module.models.ModuleListItem
import ru.hh.android.plugins.android_feature_module.models.converters.ModuleConverter
import ru.hh.android.plugins.android_feature_module.wizard.steps.ChooseAddingModulesStep
import ru.hh.android.plugins.android_feature_module.wizard.steps.ChooseApplicationModuleStep
import ru.hh.android.plugins.android_feature_module.wizard.steps.ChooseMainParametersStep


class AndroidFeatureModuleWizardModel(
        var mainParams: MainNewModuleParameters? = null,
        var forceEnabledModules: MutableList<String> = mutableListOf(),
        val enabledModules: MutableSet<ModuleListItem> = mutableSetOf(),
        val enabledApplications: MutableSet<ModuleListItem> = mutableSetOf()
) : WizardModel("Android feature module Configuration Wizard") {

    init {
        initWizardSteps()
    }


    fun setMainParameters(mainParams: MainNewModuleParameters?) {
        this.mainParams = mainParams
        forceEnabledModules = collectForceEnabledModules()
    }

    fun setModuleEnabled(moduleItem: ModuleListItem, enable: Boolean) {
        if (enable) {
            moduleItem.isEnabled = true
            enabledModules.add(moduleItem)
        } else {
            moduleItem.isEnabled = false
            enabledModules.remove(moduleItem)
        }
    }

    fun setApplicationModuleEnabled(moduleItem: ModuleListItem, enable: Boolean) {
        if (enable) {
            moduleItem.isEnabled = true
            enabledApplications.add(moduleItem)
        } else {
            moduleItem.isEnabled = false
            enabledApplications.remove(moduleItem)
        }
    }


    private fun initWizardSteps() {
        val existingModules = ProjectInfo.getAllExistingModules()
        val androidLibrariesModules = ProjectInfo.getAndroidLibrariesModules(existingModules)
        val applicationsModules = existingModules.minus(androidLibrariesModules)

        val modulesConverter = ModuleConverter()

        listOf<WizardStep<AndroidFeatureModuleWizardModel>>(
                ChooseMainParametersStep(),
                ChooseAddingModulesStep(this, modulesConverter.convert(androidLibrariesModules)),
                ChooseApplicationModuleStep(this, modulesConverter.convert(applicationsModules))
        ).forEach { add(it) }
    }

    private fun collectForceEnabledModules(): MutableList<String> {
        return mutableListOf("common", "logger", "analytics", "core-utils").apply {
            mainParams?.let { parameters ->
                if (parameters.addUIModuleDependencies) {
                    this += "base-ui"
                }

                if (parameters.needCreateAPIInterface) {
                    this += "network-source"
                    this += "network-auth-source"
                }
            }
        }
    }

}