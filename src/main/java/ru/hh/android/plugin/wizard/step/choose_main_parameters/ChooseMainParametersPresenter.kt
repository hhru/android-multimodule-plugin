package ru.hh.android.plugin.wizard.step.choose_main_parameters

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.core.BasePresenter
import ru.hh.android.plugin.model.MainParametersHolder
import ru.hh.android.plugin.wizard.PluginWizardModel


class ChooseMainParametersPresenter : BasePresenter<PluginWizardModel, ChooseMainParametersView>(), ProjectComponent {

    companion object {
        private const val INITIAL_MODULE_NAME = "mylibrary"
        private const val DEFAULT_PACKAGE_NAME_PREFIX = "ru.hh.android"
        private const val INITIAL_PACKAGE_NAME = "$DEFAULT_PACKAGE_NAME_PREFIX.mylibrary"

        private const val EDIT_BUTTON_TEXT = "Edit"
        private const val DONE_BUTTON_TEXT = "Done"

        private const val ERROR_PACKAGE_NAME_WRONG = "Error! Wrong package name!"

        private val REGEX_PACKAGE_NAME = Regex("^([A-Za-z]{1}[A-Za-z\\d_]*\\.)*[A-Za-z][A-Za-z\\d_]*\$")
    }


    private var currentPackageName: String = ""
    private var isInEditPackageMode: Boolean = false
    private var moduleNameChangedByUser: Boolean = false
    private var packageNameChangedByUser: Boolean = false
    private var wantTriggerModuleNameChanging: Boolean = false
    private var wantTriggerPackageNameChanging: Boolean = false

    private var mainParametersHolder: MainParametersHolder? = null


    override fun onCreate(model: PluginWizardModel) {
        super.onCreate(model)
        initViewComponents()
    }

    override fun onDestroy() {
        super.onDestroy()

        wantTriggerModuleNameChanging = false
        moduleNameChangedByUser = false

        wantTriggerPackageNameChanging = false
        packageNameChangedByUser = false

        isInEditPackageMode = false
        currentPackageName = ""

        mainParametersHolder = null
    }

    override fun onNextButtonClicked(model: PluginWizardModel) {
        super.onNextButtonClicked(model)
        mainParametersHolder?.let { model.setMainParameters(it) }
    }


    fun updateMainParameters(mainParametersHolder: MainParametersHolder) {
        this.mainParametersHolder = mainParametersHolder
    }

    fun onModuleNameTextChanged(newModuleName: String) {
        if (packageNameChangedByUser) {
            return
        }
        val packageName = getPackageNameFromModuleName(newModuleName)

        if (newModuleName.isNotBlank()) {
            if (wantTriggerModuleNameChanging) {
                if (!moduleNameChangedByUser) {
                    view.changePackageName(packageName)
                }
                wantTriggerModuleNameChanging = false
            } else {
                moduleNameChangedByUser = true
                view.changePackageName(packageName)
            }
        } else {
            wantTriggerModuleNameChanging = true
        }
    }

    fun onPackageNameChanged(newPackageName: String) {
        if (newPackageName.isNotBlank()) {
            if (wantTriggerPackageNameChanging) {
                if (!packageNameChangedByUser) {
                    this.currentPackageName = newPackageName
                    view.changePackageNameLabel(newPackageName)
                }
                wantTriggerModuleNameChanging = false
            } else {
                packageNameChangedByUser = true
                this.currentPackageName = newPackageName
                view.changePackageNameLabel(newPackageName)
            }
        } else {
            wantTriggerPackageNameChanging = true
        }
    }

    fun onEditPackageNameButtonClicked() {
        if (isInEditPackageMode) {
            if (checkCurrentPackageName(currentPackageName)) {
                view.showEditPackageNameLabel(true)
                view.showEditPackageNameTextField(false)
                view.changeEditPackageNameButtonText(EDIT_BUTTON_TEXT)
                isInEditPackageMode = false
            } else {
                view.showError(ERROR_PACKAGE_NAME_WRONG)
            }
        } else {
            view.showEditPackageNameLabel(false)
            view.showEditPackageNameTextField(true)
            view.changeEditPackageNameButtonText(DONE_BUTTON_TEXT)
            isInEditPackageMode = true
        }
    }


    private fun initViewComponents() {
        view.changeModuleName(INITIAL_MODULE_NAME)
        view.changePackageName(INITIAL_PACKAGE_NAME)
    }

    private fun getPackageNameFromModuleName(moduleName: String): String {
        val formattedModuleName = moduleName.replace(' ', '_').replace('-', '_')
        return "$DEFAULT_PACKAGE_NAME_PREFIX.$formattedModuleName"
    }

    private fun checkCurrentPackageName(currentPackageName: String): Boolean {
        return REGEX_PACKAGE_NAME.matches(currentPackageName)
    }

}