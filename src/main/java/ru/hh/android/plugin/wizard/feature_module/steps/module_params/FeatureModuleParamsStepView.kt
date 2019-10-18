package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import com.intellij.ui.layout.ChoicePropertyUiManager
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.visualPaddingsPanel
import ru.hh.android.plugin.extensions.EMPTY
import ru.hh.android.plugin.extensions.layout.bigTitleRow
import ru.hh.android.plugin.extensions.layout.boldLabel
import ru.hh.android.plugin.extensions.layout.onTextChange
import ru.hh.android.plugin.model.enums.PredefinedFeature
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.android.plugin.core.ui.wizard.StepView
import java.awt.Color
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.border.Border
import javax.swing.border.LineBorder


// TODO - Перенести сообщения в Bundle
class FeatureModuleParamsStepView(
        private val defaultModuleName: String,
        private val defaultPackageName: String,
        private val onModuleNameChanged: (String) -> Unit,
        private val onPackageNameChanged: (String) -> Unit,
        private val onEditPackageNameButtonClicked: () -> Unit,
        private val onPredefinedSettingsSelectionChanged: (PredefinedFeature, Boolean) -> Unit
) : StepView {

    private lateinit var moduleNameJTextField: JTextField
    private lateinit var packageNameJTextField: JTextField
    private lateinit var editPackageNameButton: JButton
    private lateinit var packageNameErrorLabel: JLabel
    private lateinit var moduleTypeChoicePropertyUiManager: ChoicePropertyUiManager<FeatureModuleType>

    private lateinit var normalBorder: Border

    private val errorColor = Color.decode("#BC3F3C")


    val selectedFeatureModuleType: FeatureModuleType
        get() = moduleTypeChoicePropertyUiManager.selected

    var currentPackageName: String = String.EMPTY
        get() = packageNameJTextField.text
        set(value) {
            field = value
            packageNameJTextField.text = value
        }

    var currentModuleName: String = String.EMPTY
        get() = moduleNameJTextField.text
        set(value) {
            field = value
            moduleNameJTextField.text = value
        }

    var editPackageNameButtonText: String = "Edit"
        set(value) {
            field = value
            editPackageNameButton.text = value
        }

    var isPackageNameTextFieldEnabled: Boolean = false
        set(value) {
            field = value
            packageNameJTextField.isEnabled = value
        }

    var isPackageNameTextFieldHasError: Boolean = false
        set(value) {
            field = value

            if (value) {
                packageNameJTextField.border = LineBorder(errorColor, 1, true)
                if (isPackageNameTextFieldEnabled) {
                    editPackageNameButton.isEnabled = false
                }
                packageNameErrorLabel.isVisible = true
            } else {
                packageNameJTextField.border = normalBorder
                if (isPackageNameTextFieldEnabled) {
                    editPackageNameButton.isEnabled = true
                }
                packageNameErrorLabel.isVisible = false
            }
        }


    override fun build(): JComponent {
        return panel {
            bigTitleRow("Android feature module")
            row { boldLabel("Configure new module: apply settings, choose folder") }

            titledRow("Module name") {
                row {
                    moduleNameJTextField = JTextField(defaultModuleName).apply {
                        onTextChange { onModuleNameChanged.invoke(moduleNameJTextField.text) }
                    }
                    moduleNameJTextField()
                }
            }

            titledRow("Package name") {
                row {
                    cell {
                        packageNameJTextField = JTextField(defaultPackageName).apply {
                            onTextChange { onPackageNameChanged.invoke(packageNameJTextField.text) }
                            isEnabled = false
                        }
                        packageNameJTextField(growX)

                        normalBorder = packageNameJTextField.border
                        packageNameJTextField()
                    }

                    cell {
                        editPackageNameButton = JButton("Edit").apply {
                            addActionListener { onEditPackageNameButtonClicked.invoke() }
                        }
                        editPackageNameButton(pushX)

                        editPackageNameButton()
                    }
                }
                row {
                    packageNameErrorLabel = JLabel("Error! Wrong package name!").apply {
                        foreground = errorColor
                        isVisible = false
                    }
                    packageNameErrorLabel()
                }
            }

            createModuleTypeSection()
            createPredefinedSettingsSection()
        }
    }


    private fun LayoutBuilder.createPredefinedSettingsSection() {
        titledRow("Predefined settings") {
            PredefinedFeature.values().forEach { setting ->
                row {
                    checkBox(
                            text = setting.uiText,
                            isSelected = setting.defaultValue,
                            actionListener = { _, component ->
                                onPredefinedSettingsSelectionChanged.invoke(setting, component.isSelected)
                            }
                    )
                    visualPaddingsPanel()
                }
            }
        }
    }

    private fun LayoutBuilder.createModuleTypeSection() {
        moduleTypeChoicePropertyUiManager = ChoicePropertyUiManager(FeatureModuleType.STANDALONE)

        titledRow("Module type") {
            buttonGroup(propertyManager = moduleTypeChoicePropertyUiManager) {
                FeatureModuleType.values().forEach { type ->
                    row {
                        radioButton(text = type.comboBoxText, id = type)
                    }
                }
            }
        }
    }

}