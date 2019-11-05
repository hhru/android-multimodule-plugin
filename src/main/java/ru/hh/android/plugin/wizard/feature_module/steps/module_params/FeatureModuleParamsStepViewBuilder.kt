package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import com.intellij.util.ui.UIUtil
import ru.hh.android.plugin.UiConstants
import ru.hh.android.plugin.core.wizard.WizardStepFormState
import ru.hh.android.plugin.core.wizard.WizardStepViewBuilder
import ru.hh.android.plugin.extensions.EMPTY
import ru.hh.android.plugin.extensions.layout.bigTitleRow
import ru.hh.android.plugin.extensions.layout.boldLabel
import ru.hh.android.plugin.extensions.layout.onTextChange
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.android.plugin.model.enums.PredefinedFeature
import java.awt.Color
import java.io.File
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.LineBorder


// TODO - Перенести сообщения в Bundle
class FeatureModuleParamsStepViewBuilder(
        private val project: Project,
        private val defaultModuleName: String,
        private val defaultPackageName: String,
        private val defaultModuleType: FeatureModuleType,
        private val onModuleNameChanged: (String) -> Unit,
        private val onPackageNameChanged: (String) -> Unit,
        private val onEditPackageNameButtonClicked: () -> Unit,
        private val onEnableAllPredefinedSettingsButtonClicked: () -> Unit,
        private val onDisableAllPredefinedSettingsButtonClicked: () -> Unit
) : WizardStepViewBuilder {

    private lateinit var moduleNameJTextField: JTextField
    private lateinit var packageNameJTextField: JTextField
    private lateinit var editPackageNameButton: JButton
    private lateinit var packageNameErrorLabel: JLabel
    private lateinit var moduleTypeButtonGroup: ButtonGroup
    private lateinit var customFolderButton: JButton
    private lateinit var customFolderPathTextField: JTextField
    private lateinit var customFolderPathCommentLabel: JLabel

    private lateinit var normalTextFieldBorder: Border

    private val predefinedFeaturesCheckboxes = mutableMapOf<PredefinedFeature, JCheckBox>()
    private val errorColor = Color.decode(UiConstants.DARK_THEME_ERROR_COLOR)

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
                packageNameJTextField.border = normalTextFieldBorder
                if (isPackageNameTextFieldEnabled) {
                    editPackageNameButton.isEnabled = true
                }
                packageNameErrorLabel.isVisible = false
            }
        }

    private val selectedFeatureModuleType: FeatureModuleType
        get() {
            val selectedItem = moduleTypeButtonGroup.selection
            return FeatureModuleType.valueOf(selectedItem.actionCommand)
        }

    private val customModulePath: String
        get() {
            return "/${customFolderPathTextField.text}"
        }


    override fun build(): JComponent {
        return panel {
            bigTitleRow("Android feature module")
            row { boldLabel("Configure new module: apply settings, choose folder") }

            createModuleNameSection()
            createPackageNameSection()
            createModuleTypeSection()
            createPredefinedSettingsSection()
        }
    }

    override fun collectFormState(): WizardStepFormState {
        val enabledFeatures = predefinedFeaturesCheckboxes
                .filter { it.value.isSelected }
                .map { it.key }

        return FeatureModuleParamsFormState(
                moduleName = currentModuleName,
                packageName = currentPackageName,
                moduleType = selectedFeatureModuleType,
                customModuleTypePath = customModulePath,
                enabledFeatures = enabledFeatures
        )
    }


    fun enableAllPredefinedSettings(enable: Boolean) {
        for (checkbox in predefinedFeaturesCheckboxes.values) {
            checkbox.isSelected = enable
        }
    }


    private fun LayoutBuilder.createModuleNameSection() {
        titledRow("Module name") {
            row {
                moduleNameJTextField = JTextField(defaultModuleName).apply {
                    onTextChange { onModuleNameChanged.invoke(moduleNameJTextField.text) }
                }
                moduleNameJTextField()
            }
        }
    }

    private fun LayoutBuilder.createPackageNameSection() {
        titledRow("Package name") {
            row {
                cell {
                    packageNameJTextField = JTextField(defaultPackageName).apply {
                        onTextChange { onPackageNameChanged.invoke(packageNameJTextField.text) }
                        isEnabled = false
                    }
                    packageNameJTextField(growX)

                    normalTextFieldBorder = packageNameJTextField.border
                    packageNameJTextField()
                }

                cell {
                    editPackageNameButton = JButton("Edit").apply {
                        addActionListener { onEditPackageNameButtonClicked.invoke() }
                    }

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
    }

    private fun LayoutBuilder.createPredefinedSettingsSection() {
        titledRow("Predefined features") {
            PredefinedFeature.values().forEach { feature ->
                row {
                    val featureCheckBox = checkBox(
                            text = feature.uiText,
                            isSelected = feature.defaultValue
                    )
                    predefinedFeaturesCheckboxes[feature] = featureCheckBox
                }
            }
            row {
                cell {
                    button("Enable all") {
                        onEnableAllPredefinedSettingsButtonClicked.invoke()
                    }
                    button("Disable all") {
                        onDisableAllPredefinedSettingsButtonClicked.invoke()
                    }
                }
            }
        }
    }


    private fun LayoutBuilder.createModuleTypeSection() {
        titledRow("Module type") {
            moduleTypeButtonGroup = ButtonGroup()
            FeatureModuleType.values().forEach { type ->
                row {
                    val radioButton = JRadioButton(type.radioButtonText, type == defaultModuleType).apply {
                        actionCommand = type.name
                        addActionListener {
                            val newType = selectedFeatureModuleType

                            val isCustomFolderVisible = newType == FeatureModuleType.CUSTOM_PATH
                            showCustomModulePathControls(isCustomFolderVisible)
                        }
                    }
                    moduleTypeButtonGroup.add(radioButton)

                    radioButton()
                }
            }

            row {
                cell {
                    customFolderPathTextField = JTextField()
                    customFolderPathTextField(growX)
                    customFolderPathTextField()
                }
                cell {
                    customFolderButton = JButton("Browse").apply {
                        addActionListener {
                            val fileChooser = JFileChooser().apply {
                                isMultiSelectionEnabled = false
                                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                project.basePath?.let { currentDirectory = File(it) }
                            }

                            val result = fileChooser.showDialog(customFolderPathTextField, "Save")
                            if (result == JFileChooser.APPROVE_OPTION) {
                                val selectedFile = fileChooser.selectedFile
                                customFolderPathTextField.text = project.basePath?.let { projectPath ->
                                    selectedFile.relativeTo(File(projectPath)).toString()
                                } ?: String.EMPTY
                            }
                        }
                    }
                    customFolderButton()
                }
            }
            row {
                customFolderPathCommentLabel = JLabel("Choose folder for feature module (e.g. 'feature/feature-profile', 'core/some-core', etc)").apply {
                    font = font.deriveFont(font.size2D - 2.0f)
                    isFocusable = false
                    foreground = UIUtil.getContextHelpForeground()
                }
                customFolderPathCommentLabel()
            }
        }
    }

    private fun showCustomModulePathControls(show: Boolean) {
        customFolderButton.isVisible = show
        customFolderPathTextField.isVisible = show
        customFolderPathCommentLabel.isVisible = show
    }

}