package ru.hh.android.plugin.wizard.feature_module.steps.module_params

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.panel
import com.intellij.util.ui.UIUtil
import ru.hh.android.plugin.core.framework_ui.view.ModuleNamePanel
import ru.hh.android.plugin.core.wizard.WizardStepFormState
import ru.hh.android.plugin.core.wizard.WizardStepViewBuilder
import ru.hh.android.plugin.extensions.EMPTY
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.android.plugin.model.enums.PredefinedFeature
import ru.hh.plugins.extensions.layout.bigTitleRow
import ru.hh.plugins.extensions.layout.boldLabel
import java.io.File
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JRadioButton
import javax.swing.JTextField


// TODO - Перенести сообщения в Bundle
@Suppress("UnstableApiUsage")
class FeatureModuleParamsStepViewBuilder(
    private val project: Project,
    private val defaultModuleType: FeatureModuleType,
    private val onEnableAllPredefinedSettingsButtonClicked: () -> Unit,
    private val onDisableAllPredefinedSettingsButtonClicked: () -> Unit
) : WizardStepViewBuilder {

    private lateinit var moduleNamePanel: ModuleNamePanel
    private lateinit var moduleTypeButtonGroup: ButtonGroup
    private lateinit var customFolderButton: JButton
    private lateinit var customFolderPathTextField: JTextField
    private lateinit var customFolderPathCommentLabel: JLabel


    private val predefinedFeaturesCheckboxes = mutableMapOf<PredefinedFeature, JCheckBox>()


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
            moduleNamePanel = ModuleNamePanel()
            moduleNamePanel.create(this)
            createModuleTypeSection()
            createPredefinedSettingsSection()
        }
    }

    override fun collectFormState(): WizardStepFormState {
        val enabledFeatures = predefinedFeaturesCheckboxes
            .filter { it.value.isSelected }
            .map { it.key }

        return FeatureModuleParamsFormState(
            moduleName = moduleNamePanel.getModuleName(),
            packageName = moduleNamePanel.getPackageName(),
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


    private fun LayoutBuilder.createPredefinedSettingsSection() {
        titledRow("Predefined features") {
            PredefinedFeature.values().forEach { feature ->
                row {
                    val featureCheckBox = checkBox(
                        text = feature.uiText,
                        isSelected = feature.defaultValue
                    ).component
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
                customFolderPathCommentLabel =
                    JLabel("Choose folder for feature module (e.g. 'feature/feature-profile', 'core/some-core', etc)").apply {
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