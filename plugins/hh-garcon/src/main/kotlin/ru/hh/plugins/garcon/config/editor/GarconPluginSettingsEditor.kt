package ru.hh.plugins.garcon.config.editor

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import ru.hh.plugins.PluginsConstants
import ru.hh.plugins.garcon.config.GarconPluginConfig
import ru.hh.plugins.views.layouts.fileChooserButton
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JTextField


class GarconPluginSettingsEditor(
    private val initialConfigFilePath: String,
    private val initialEnableDebugMode: Boolean,
    private val initialScreenPageObjectTemplatePath: String,
    private val initialRvItemPageObjectTemplatePath: String
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


    // property-references doesn't work >_<
    fun createComponent(project: Project): JComponent {
        return panel {
            titledRow("Config file path:") {
                row {
                    configFilePathTextField = JTextField(initialConfigFilePath)
                    configFilePathTextField(CCFlags.growX)
                }
                row {
                    fileChooserButton(
                        project = project,
                        buttonText = "Choose config file",
                        filterText = "Config files",
                        fileChooserButtonText = "Save",
                        filterFilesExtensions = arrayOf(PluginsConstants.YAML_FILES_FILTER_EXTENSION)
                    ) { selectedFile ->
                        configFilePathTextField.text = selectedFile.absolutePath
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