package ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.wizard.WizardStep
import org.jetbrains.kotlin.idea.util.onTextChange
import ru.hh.android.plugin.feature_module.core.BaseWizardStep
import ru.hh.android.plugin.feature_module.model.MainParametersHolder
import ru.hh.android.plugin.feature_module.model.enums.FeatureModuleType
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.ChooseModulesWizardStep
import javax.swing.*


class ChooseMainParametersWizardStep(
        override val presenter: ChooseMainParametersPresenter
) : BaseWizardStep<PluginWizardModel, ChooseMainParametersView>(), ChooseMainParametersView {

    override lateinit var contentPanel: JPanel

    private lateinit var libraryNameTextField: JTextField
    private lateinit var moduleNameTextField: JTextField
    private lateinit var packageNameTextField: JTextField
    private lateinit var editPackageNameButton: JButton
    private lateinit var packageNameLabel: JLabel
    private lateinit var moduleTypeComboBox: JComboBox<*>
    private lateinit var enableMoxyCheckBox: JCheckBox
    private lateinit var addUIModulesDependenciesCheckBox: JCheckBox
    private lateinit var createAPIInterfaceCheckBox: JCheckBox
    private lateinit var createRepositoryWithInteractorCheckBox: JCheckBox
    private lateinit var createInterfaceForRepositoryCheckBox: JCheckBox


    override fun onCreate() {
        super.onCreate()

        libraryNameTextField.onTextChange { presenter.onLibraryNameTextChanged(libraryNameTextField.text) }
        moduleNameTextField.onTextChange { presenter.onModuleNameTextChanged(moduleNameTextField.text) }
        packageNameTextField.onTextChange { presenter.onPackageNameChanged(packageNameTextField.text) }
        editPackageNameButton.addActionListener { presenter.onEditPackageNameButtonClicked() }
    }

    override fun onNext(model: PluginWizardModel): WizardStep<*> {
        val nextStep = super.onNext(model)

        if (nextStep is ChooseModulesWizardStep) {
            presenter.onNextButtonClicked(collectMainParameters())
        }

        return nextStep
    }


    override fun changeLibraryName(libraryName: String) {
        libraryNameTextField.text = libraryName
    }

    override fun changeModuleName(newModuleName: String) {
        moduleNameTextField.text = newModuleName
    }

    override fun changePackageName(newPackageName: String) {
        packageNameLabel.text = newPackageName
        packageNameTextField.text = newPackageName
    }

    override fun changePackageNameLabel(newPackageName: String) {
        packageNameLabel.text = newPackageName
    }

    override fun showEditPackageNameLabel(needShow: Boolean) {
        packageNameLabel.isVisible = needShow
    }

    override fun showEditPackageNameTextField(needShow: Boolean) {
        packageNameTextField.isVisible = needShow
    }

    override fun changeEditPackageNameButtonText(buttonText: String) {
        editPackageNameButton.text = buttonText
    }

    override fun showError(errorText: String) {
        DialogBuilder().apply {
            setErrorText(errorText)
            show()
        }
    }


    private fun collectMainParameters(): MainParametersHolder {
        return MainParametersHolder(
                libraryName = libraryNameTextField.text,
                moduleName = moduleNameTextField.text,
                packageName = packageNameTextField.text,
                moduleType = moduleTypeComboBox.selectedItem as FeatureModuleType,
                enableMoxy = enableMoxyCheckBox.isSelected,
                addUIModulesDependencies = addUIModulesDependenciesCheckBox.isSelected,
                needCreateAPIInterface = createAPIInterfaceCheckBox.isSelected,
                needCreateRepositoryWithInteractor = createRepositoryWithInteractorCheckBox.isSelected,
                needCreateInterfaceForRepository = createInterfaceForRepositoryCheckBox.isSelected
        )
    }


    private fun createUIComponents() {
        initModuleTypeComboBox()

        libraryNameTextField = JTextField()
        moduleNameTextField = JTextField()
        packageNameLabel = JLabel()
        packageNameTextField = JTextField()
        editPackageNameButton = JButton()
    }

    private fun initModuleTypeComboBox() {
        moduleTypeComboBox = ComboBox<FeatureModuleType>().apply {
            setModel(EnumComboBoxModel(FeatureModuleType::class.java))
        }
    }

}