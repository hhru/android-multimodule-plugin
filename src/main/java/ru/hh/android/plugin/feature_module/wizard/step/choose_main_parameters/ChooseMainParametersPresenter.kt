package ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.core.BasePresenter
import ru.hh.android.plugin.feature_module.model.MainParametersHolder


class ChooseMainParametersPresenter(
        private var wantTriggerModuleNameChanging: Boolean = false,
        private var moduleNameChangedByUser: Boolean = false,
        private var wantTriggerPackageNameChanging: Boolean = false,
        private var packageNameChangedByUser: Boolean = false,
        private var isInEditPackageMode: Boolean = false,
        private var currentPackageName: String = ""
) : BasePresenter<ChooseMainParametersView>(), ProjectComponent {

    companion object {
        private const val INITIAL_LIBRARY_NAME = "My Library"
        private const val INITIAL_MODULE_NAME = "mylibrary"
        private const val DEFAULT_PACKAGE_NAME_PREFIX = "ru.hh.android"
        private const val INITIAL_PACKAGE_NAME = "$DEFAULT_PACKAGE_NAME_PREFIX.mylibrary"

        private const val EDIT_BUTTON_TEXT = "Edit"
        private const val DONE_BUTTON_TEXT = "Done"

        private const val ERROR_PACKAGE_NAME_WRONG = "Error! Wrong package name!"

        private val REGEX_PACKAGE_NAME = Regex("^([A-Za-z]{1}[A-Za-z\\d_]*\\.)*[A-Za-z][A-Za-z\\d_]*\$")
    }


    override fun onCreate() {
        super.onCreate()
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
    }


    fun onNextButtonClicked(mainParametersHolder: MainParametersHolder) {
        // todo - handle main parameters.
    }

    fun onLibraryNameTextChanged(libraryName: String) {
        if (moduleNameChangedByUser) {
            return
        }

        wantTriggerModuleNameChanging = true

        val moduleName = getModuleNameFromLibraryName(libraryName)
        view.changeModuleName(moduleName)
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
        view.changeLibraryName(INITIAL_LIBRARY_NAME)
        view.changeModuleName(INITIAL_MODULE_NAME)
        view.changePackageName(INITIAL_PACKAGE_NAME)
    }

    private fun getModuleNameFromLibraryName(libraryName: String): String {
        val m1 = libraryName.trim().replace(" ", "", true)
        val m2 = m1.replace('-', '_', true)

        return m2.toLowerCase()
    }

    private fun getPackageNameFromModuleName(moduleName: String): String {
        return "$DEFAULT_PACKAGE_NAME_PREFIX.$moduleName"
    }

    private fun checkCurrentPackageName(currentPackageName: String): Boolean {
        return REGEX_PACKAGE_NAME.matches(currentPackageName)
    }

}