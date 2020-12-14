package ru.hh.plugins.geminio.config.editor

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import ru.hh.plugins.PluginsConstants
import ru.hh.plugins.views.layouts.fileChooserButton
import ru.hh.plugins.geminio.config.GeminioPluginConfig
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JTextField


class GeminioPluginSettingsEditor(
    private val initialConfigFilePath: String,
    private val initialTemplatesRootDirPath: String,
    private val initialNameForNewGroup: String,
) {

    companion object {

        fun newInstance(settings: GeminioPluginSettings): GeminioPluginSettingsEditor {
            return with(settings) {
                GeminioPluginSettingsEditor(
                    initialConfigFilePath = config.configFilePath,
                    initialTemplatesRootDirPath = config.templatesRootDirPath,
                    initialNameForNewGroup = config.groupsNames.forNewGroup,
                )
            }
        }

    }

    private lateinit var configFilePathTextField: JTextField
    private lateinit var templatesRootDirPathTextField: JTextField
    private lateinit var nameForNewGroupTextField: JTextField
    private lateinit var nameForGenerateGroupTextField: JTextField
    private lateinit var enableDebugModeCheckBox: JCheckBox


    fun createComponent(project: Project): JComponent? {
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
                    templatesRootDirPathTextField = JTextField(initialTemplatesRootDirPath)
                    templatesRootDirPathTextField(CCFlags.growX)
                }
            }
            titledRow("Groups names:") {
                row {
                    nameForNewGroupTextField = JTextField(initialNameForNewGroup)
                    nameForNewGroupTextField(CCFlags.growX)
                }
            }
        }
    }

    fun isModified(settings: GeminioPluginSettings): Boolean {
        return with(settings) {
            config.configFilePath != configFilePathTextField.text
                    || config.templatesRootDirPath != templatesRootDirPathTextField.text
                    || config.groupsNames.forNewGroup != nameForNewGroupTextField.text
        }
    }

    fun applyNewConfiguration(settings: GeminioPluginSettings) {
        if (settings.config.configFilePath != configFilePathTextField.text) {
            settings.tryLoadFromConfigFile(configFilePathTextField.text)

            templatesRootDirPathTextField.text = settings.config.templatesRootDirPath
            nameForNewGroupTextField.text = settings.config.groupsNames.forNewGroup
        } else {
            settings.config = settings.config.copy(
                templatesRootDirPath = templatesRootDirPathTextField.text,
                groupsNames = GeminioPluginConfig.GroupsNames(
                    forNewGroup = nameForNewGroupTextField.text,
                ),
            )
        }
    }

}