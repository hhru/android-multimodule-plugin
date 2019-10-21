package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.core.wizard.WizardStepPresenter
import ru.hh.android.plugin.extensions.HYPHEN
import ru.hh.android.plugin.extensions.SPACE
import ru.hh.android.plugin.extensions.UNDERSCORE
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardModel


// TODO - Вынести сообщения в bundle
class FeatureModuleParamsPresenter(
        private val project: Project
) : WizardStepPresenter<FeatureModuleWizardModel, FeatureModuleParamsStepView, FeatureModuleParamsFormState>() {

    companion object {
        private val REGEX_PACKAGE_NAME = Regex("^([A-Za-z]{1}[A-Za-z\\d_]*\\.)*[A-Za-z][A-Za-z\\d_]*\$")
    }


    private var isPackageNameInEditMode = false
    private var isPackageNameWasChangedByUser = false
    private var wantTriggerPackageNameChanged = false


    override fun validateForm(formState: FeatureModuleParamsFormState): Boolean {
        return checkCurrentPackageName(formState.packageName) &&
                (formState.moduleType != FeatureModuleType.CUSTOM_PATH || formState.customModuleTypePath.isNotBlank())
    }

    override fun updateModel(model: FeatureModuleWizardModel, formState: FeatureModuleParamsFormState) {
        model.params = FeatureModuleParamsFormStateConverter().convert(formState)
    }


    fun onModuleNameChanged(newModuleName: String) {
        if (isPackageNameWasChangedByUser) {
            return
        }

        wantTriggerPackageNameChanged = true
        view.setCurrentPackageName(getPackageNameFromModuleName(newModuleName))
    }

    fun onPackageNameChanged(newPackageName: String) {
        if (newPackageName.isNotBlank()) {
            if (wantTriggerPackageNameChanged) {
                wantTriggerPackageNameChanged = false
            } else {
                isPackageNameWasChangedByUser = true
            }

            view.showPackageNameTextFieldError(!checkCurrentPackageName(newPackageName))
        }
    }

    fun onEditPackageNameButtonClicked() {
        val newEditPackageNameButtonText = if (isPackageNameInEditMode) {
            "Edit"
        } else {
            "Done"
        }
        view.changeEditPackageNameButtonText(newEditPackageNameButtonText)
        view.enablePackageNameTextField(!isPackageNameInEditMode)
        isPackageNameInEditMode = !isPackageNameInEditMode
    }

    fun onEnableAllPredefinedSettingsButtonClicked() {
        view.enableAllPredefinedSettings(true)
    }

    fun onDisableAllPredefinedSettingsButtonClicked() {
        view.enableAllPredefinedSettings(false)
    }


    private fun getPackageNameFromModuleName(moduleName: String): String {
        val formattedModuleName = moduleName
                .replace(Char.SPACE, Char.UNDERSCORE)
                .replace(Char.HYPHEN, Char.UNDERSCORE)
        return "${PluginConstants.DEFAULT_PACKAGE_NAME_PREFIX}.$formattedModuleName"
    }

    private fun checkCurrentPackageName(currentPackageName: String): Boolean {
        return REGEX_PACKAGE_NAME.matches(currentPackageName)
    }


}