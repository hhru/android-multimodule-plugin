package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.model.MainParametersHolder
import ru.hh.android.plugin.model.enums.PredefinedFeature
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardModel
import javax.swing.JComponent


// TODO - Перенести сообщения в Bundle
class FeatureModuleParamsWizardStep : WizardStep<FeatureModuleWizardModel>() {

    companion object {
        private const val DEFAULT_MODULE_NAME = "mylibrary"
        private const val DEFAULT_PACKAGE_NAME_PREFIX = "ru.hh.android"
        private const val DEFAULT_PACKAGE_NAME = "$DEFAULT_PACKAGE_NAME_PREFIX.$DEFAULT_MODULE_NAME"

        private val REGEX_PACKAGE_NAME = Regex("^([A-Za-z]{1}[A-Za-z\\d_]*\\.)*[A-Za-z][A-Za-z\\d_]*\$")
    }


    private val uiBuilder = FeatureModuleParamsStepView(
            defaultModuleName = DEFAULT_MODULE_NAME,
            defaultPackageName = DEFAULT_PACKAGE_NAME,
            onModuleNameChanged = { onModuleNameChanged(it) },
            onPackageNameChanged = { onPackageNameChanged(it) },
            onEditPackageNameButtonClicked = { onEditPackageNameButtonClicked() },
            onPredefinedSettingsSelectionChanged = { setting, isSelected ->
                onPredefinedSettingsSelectionChanged(setting, isSelected)
            }
    )

    private val enabledSettings = mutableSetOf<PredefinedFeature>()

    private var isPackageNameInEditMode = false
    private var isPackageNameWasChangedByUser = false
    private var wantTriggerPackageNameChanged = false


    override fun prepare(state: WizardNavigationState?): JComponent {
        return uiBuilder.build()
    }

    override fun onNext(model: FeatureModuleWizardModel): WizardStep<*> {
        updateModel(model)
        return super.onNext(model)
    }


    private fun onModuleNameChanged(newModuleName: String) {
        if (isPackageNameWasChangedByUser) {
            return
        }

        wantTriggerPackageNameChanged = true
        uiBuilder.currentPackageName = getPackageNameFromModuleName(newModuleName)
    }

    private fun onPackageNameChanged(newPackageName: String) {
        if (newPackageName.isNotBlank()) {
            if (wantTriggerPackageNameChanged) {
                wantTriggerPackageNameChanged = false
            } else {
                isPackageNameWasChangedByUser = true
            }

            uiBuilder.isPackageNameTextFieldHasError = !checkCurrentPackageName(newPackageName)
        }
    }

    private fun onPredefinedSettingsSelectionChanged(feature: PredefinedFeature, isSelected: Boolean) {
        if (isSelected) {
            enabledSettings.add(feature)
        } else {
            enabledSettings.remove(feature)
        }
    }

    private fun onEditPackageNameButtonClicked() {
        uiBuilder.editPackageNameButtonText = if (isPackageNameInEditMode) {
            "Edit"
        } else {
            "Done"
        }
        uiBuilder.isPackageNameTextFieldEnabled = !isPackageNameInEditMode
        isPackageNameInEditMode = !isPackageNameInEditMode
    }

    private fun getPackageNameFromModuleName(moduleName: String): String {
        val formattedModuleName = moduleName.replace(' ', '_').replace('-', '_')
        return "${DEFAULT_PACKAGE_NAME_PREFIX}.$formattedModuleName"
    }

    private fun checkCurrentPackageName(currentPackageName: String): Boolean {
        return REGEX_PACKAGE_NAME.matches(currentPackageName)
    }

    private fun updateModel(model: FeatureModuleWizardModel) {
        model.params = collectMainParams()
    }

    private fun collectMainParams(): MainParametersHolder {
        return MainParametersHolder(
                moduleName = uiBuilder.currentModuleName,
                packageName = uiBuilder.currentPackageName,
                moduleType = uiBuilder.selectedFeatureModuleType,
                enabledSettings = enabledSettings.toList()
        )
    }

}