package ru.hh.plugins.garcon.config.editor

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import ru.hh.plugins.garcon.config.GarconPluginConfig
import java.io.File
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JTextField
import javax.swing.filechooser.FileNameExtensionFilter


class GarconPluginSettingsEditor(
    val initialConfigFilePath: String,
    val initialEnableDebugMode: Boolean,
    val initialScreenPageObjectTemplatePath: String,
    val initialRvItemPageObjectTemplatePath: String
) {

    companion object {

        fun newInstance(settings: GarconPluginSettings): GarconPluginSettingsEditor {
            return with(settings) {
                GarconPluginSettingsEditor(
                    initialConfigFilePath = config.configFilePath,
                    initialEnableDebugMode = config.enableDebugMode,
                    initialScreenPageObjectTemplatePath = config.templatesPaths.screenPageObjectTemplatePath,
                    initialRvItemPageObjectTemplatePath = config.templatesPaths.rvItemPageObjectTemplatePath
                )
            }
        }

    }

    private lateinit var configFilePathTextField: JTextField
    private lateinit var screenPageObjectTemplatePathTextField: JTextField
    private lateinit var rvItemPageObjectTemplatePathTextField: JTextField
    private lateinit var enableDebugModeCheckBox: JCheckBox


    @Suppress("UnstableApiUsage")
    fun createComponent(project: Project): JComponent? {
        return panel {
            titledRow("Config file path:") {
                row {
                    configFilePathTextField = JTextField(initialConfigFilePath)
                    configFilePathTextField(CCFlags.growX)
                }
                row {
                    button("Choose config file") {
                        val fileChooser = JFileChooser().apply {
                            isMultiSelectionEnabled = false
                            fileSelectionMode = JFileChooser.FILES_ONLY
                            fileFilter = FileNameExtensionFilter("Config files", "yaml")
                            project.basePath?.let { currentDirectory = File(it) }
                        }

                        val result = fileChooser.showDialog(screenPageObjectTemplatePathTextField, "Save")
                        if (result == JFileChooser.APPROVE_OPTION) {
                            configFilePathTextField.text = fileChooser.selectedFile.absolutePath
                        }

                    }
                }
            }
            titledRow("Templates paths:") {
                row {
                    screenPageObjectTemplatePathTextField = JTextField(initialScreenPageObjectTemplatePath)
                    screenPageObjectTemplatePathTextField(CCFlags.growX)
                }
                row {
                    rvItemPageObjectTemplatePathTextField = JTextField(initialRvItemPageObjectTemplatePath)
                    rvItemPageObjectTemplatePathTextField(CCFlags.growX)
                }
            }
            titledRow("Default package: ") {

            }

            titledRow("Debug mode") {
                row {
                    enableDebugModeCheckBox = checkBox(
                        text = "Enable debug mode",
                        isSelected = initialEnableDebugMode
                    ).component
                    enableDebugModeCheckBox()
                }
            }
        }
    }

    fun isModified(settings: GarconPluginSettings): Boolean {
        return with(settings) {
            config.configFilePath != configFilePathTextField.text
                    || config.templatesPaths.screenPageObjectTemplatePath != screenPageObjectTemplatePathTextField.text
                    || config.templatesPaths.rvItemPageObjectTemplatePath != rvItemPageObjectTemplatePathTextField.text
                    || config.enableDebugMode != enableDebugModeCheckBox.isSelected
        }
    }

    fun applyNewConfiguration(settings: GarconPluginSettings) {
        if (settings.config.configFilePath != configFilePathTextField.text) {
            settings.tryLoadFromConfigFile(configFilePathTextField.text)

            screenPageObjectTemplatePathTextField.text = settings.config.templatesPaths.screenPageObjectTemplatePath
            rvItemPageObjectTemplatePathTextField.text = settings.config.templatesPaths.rvItemPageObjectTemplatePath
            enableDebugModeCheckBox.isSelected = settings.config.enableDebugMode
        } else {
            settings.config = settings.config.copy(
                enableDebugMode = enableDebugModeCheckBox.isSelected,
                templatesPaths = GarconPluginConfig.TemplatesPaths(
                    screenPageObjectTemplatePath = screenPageObjectTemplatePathTextField.text,
                    rvItemPageObjectTemplatePath = rvItemPageObjectTemplatePathTextField.text
                )
            )
        }
    }

}