package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.core.wizard.BaseWizardStep
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.android.plugin.wizard.feature_module.FeatureModuleWizardModel


class FeatureModuleParamsWizardStep(
        project: Project,
        model: FeatureModuleWizardModel
) : FeatureModuleParamsStepView, BaseWizardStep<
        FeatureModuleWizardModel,
        FeatureModuleParamsStepView,
        FeatureModuleParamsFormState,
        FeatureModuleParamsStepViewBuilder,
        FeatureModuleParamsPresenter>(model, project) {

    override fun getViewBuilder(): FeatureModuleParamsStepViewBuilder {
        return FeatureModuleParamsStepViewBuilder(
                project = project,
                defaultModuleName = PluginConstants.DEFAULT_FEATURE_MODULE_NAME,
                defaultPackageName = PluginConstants.DEFAULT_PACKAGE_NAME,
                defaultModuleType = FeatureModuleType.CUSTOM_PATH,
                onModuleNameChanged = { presenter.onModuleNameChanged(it) },
                onPackageNameChanged = { presenter.onPackageNameChanged(it) },
                onEditPackageNameButtonClicked = { presenter.onEditPackageNameButtonClicked() },
                onEnableAllPredefinedSettingsButtonClicked = {
                    presenter.onEnableAllPredefinedSettingsButtonClicked()
                },
                onDisableAllPredefinedSettingsButtonClicked = {
                    presenter.onDisableAllPredefinedSettingsButtonClicked()
                }
        )
    }

    override fun getPresenter(project: Project): FeatureModuleParamsPresenter {
        return FeatureModuleParamsPresenter(project)
    }


    override fun setCurrentPackageName(packageName: String) {
        uiBuilder.currentPackageName = packageName
    }

    override fun changeEditPackageNameButtonText(editPackageNameButtonText: String) {
        uiBuilder.editPackageNameButtonText = editPackageNameButtonText
    }

    override fun enablePackageNameTextField(enable: Boolean) {
        uiBuilder.isPackageNameTextFieldEnabled = enable
    }

    override fun showPackageNameTextFieldError(show: Boolean) {
        uiBuilder.isPackageNameTextFieldHasError = show
    }

    override fun enableAllPredefinedSettings(enable: Boolean) {
        uiBuilder.enableAllPredefinedSettings(enable)
    }

}