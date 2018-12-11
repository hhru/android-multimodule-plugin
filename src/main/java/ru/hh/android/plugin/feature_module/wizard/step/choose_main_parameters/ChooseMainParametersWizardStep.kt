package ru.hh.android.plugin.feature_module.wizard.step.choose_main_parameters

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import ru.hh.android.plugin.feature_module.model.MainParametersHolder
import ru.hh.android.plugin.feature_module.model.enums.FeatureModuleType
import ru.hh.android.plugin.feature_module.wizard.PluginWizardModel
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.ChooseModulesWizardStep
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class ChooseMainParametersWizardStep(
        private val controller: ChooseMainParametersController,
        private var isPackageNameInEditMode: Boolean = false
) : WizardStep<PluginWizardModel>() {

    private lateinit var contentPanel: JPanel
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


    override fun prepare(state: WizardNavigationState): JComponent = contentPanel

    override fun onNext(model: PluginWizardModel): WizardStep<*> {
        val nextStep = super.onNext(model)

        if (nextStep is ChooseModulesWizardStep) {
            controller.onNextButtonClicked(collectMainParameters())
        }

        return nextStep
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

        setupLibraryNameChangedListener()
        setupModuleNameChangedListener()
        setupPackageNameChangedListener()
        setupEditPackageNameButtonListener()
    }


    private fun setupModuleNameChangedListener() {
        moduleNameTextField = JTextField()
    }

    private fun setupLibraryNameChangedListener() {
        libraryNameTextField = JTextField().apply {
            document.addDocumentListener(object : DocumentListener {

                override fun changedUpdate(e: DocumentEvent) {
                    onLibraryNameChanged()
                }

                override fun removeUpdate(e: DocumentEvent) {
                    onLibraryNameChanged()
                }

                override fun insertUpdate(e: DocumentEvent) {
                    onLibraryNameChanged()
                }

            })
        }
    }

    private fun setupPackageNameChangedListener() {
        packageNameLabel = JLabel()

        packageNameTextField = JTextField().apply {
            document.addDocumentListener(object : DocumentListener {

                override fun changedUpdate(e: DocumentEvent) {
                    onPackageNameChanged()
                }

                override fun removeUpdate(e: DocumentEvent) {
                    onPackageNameChanged()
                }

                override fun insertUpdate(e: DocumentEvent) {
                    onPackageNameChanged()
                }

            })
        }
    }

    private fun setupEditPackageNameButtonListener() {
        editPackageNameButton = JButton().apply {
            addActionListener {
                isPackageNameInEditMode = !isPackageNameInEditMode

                editPackageNameButton.text = if (isPackageNameInEditMode) "Done" else "Edit"
                packageNameLabel.isVisible = !isPackageNameInEditMode
                packageNameTextField.isVisible = isPackageNameInEditMode
            }
        }
    }


    private fun initModuleTypeComboBox() {
        moduleTypeComboBox = ComboBox<FeatureModuleType>().apply {
            setModel(EnumComboBoxModel(FeatureModuleType::class.java))
        }
    }


    private fun onLibraryNameChanged() {
        val newLibraryName = libraryNameTextField.text
        val m1 = newLibraryName.replace(" ", "", true)
        val m2 = m1.replace('-', '_', true)
        val m3 = m2.toLowerCase()


        moduleNameTextField.text = m3
    }

    private fun onPackageNameChanged() {
        val currentPackageName = packageNameTextField.text
        packageNameLabel.text = currentPackageName
    }

}