package ru.hh.plugins.geminio.config.editor

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import ru.hh.plugins.PluginsConstants
import ru.hh.plugins.geminio.config.GeminioPluginConfig
import ru.hh.plugins.views.layouts.fileChooserButton
import javax.swing.JComponent
import javax.swing.JTextField


class GeminioPluginSettingsEditor(
    private val initialConfigFilePath: String,
    private val initialTemplatesRootDirPath: String,
    private val initialModulesTemplatesRootDirPath: String,
    private val initialNameForNewGroup: String,
    private val initialNameForNewModulesGroup: String,
) {

    companion object {

        fun newInstance(settings: GeminioPluginSettings): GeminioPluginSettingsEditor {
            return with(settings) {
                GeminioPluginSettingsEditor(
                    initialConfigFilePath = config.configFilePath,
                    initialTemplatesRootDirPath = config.templatesRootDirPath,
                    initialModulesTemplatesRootDirPath = config.modulesTemplatesRootDirPath,
                    initialNameForNewGroup = config.groupsNames.forNewGroup,
                    initialNameForNewModulesGroup = config.groupsNames.forNewModulesGroup,
                )
            }
        }

    }

    private lateinit var configFilePathTextField: JTextField
    private lateinit var templatesRootDirPathTextField: JTextField
    private lateinit var modulesTemplatesRootDirPathTextField: JTextField
    private lateinit var nameForNewGroupTextField: JTextField
    private lateinit var nameForNewModulesGroupTextField: JTextField


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
            titledRow("Templates paths") {
                row("Templates:") {
                    templatesRootDirPathTextField = JTextField(initialTemplatesRootDirPath)
                    templatesRootDirPathTextField(CCFlags.growX)
                }
                row("Modules templates:") {
                    modulesTemplatesRootDirPathTextField = JTextField(initialModulesTemplatesRootDirPath)
                    modulesTemplatesRootDirPathTextField(CCFlags.growX)
                }
            }
            titledRow("Groups names") {
                row("Templates group:") {
                    nameForNewGroupTextField = JTextField(initialNameForNewGroup)
                    nameForNewGroupTextField(CCFlags.growX)
                }
                row("Modules templates group:") {
                    nameForNewModulesGroupTextField = JTextField(initialNameForNewModulesGroup)
                    nameForNewModulesGroupTextField(CCFlags.growX)
                }
            }
        }
    }

    fun isModified(settings: GeminioPluginSettings): Boolean {
        return with(settings) {
            config.configFilePath != configFilePathTextField.text
                    || config.templatesRootDirPath != templatesRootDirPathTextField.text
                    || config.modulesTemplatesRootDirPath != modulesTemplatesRootDirPathTextField.text
                    || config.groupsNames.forNewGroup != nameForNewGroupTextField.text
                    || config.groupsNames.forNewModulesGroup != nameForNewModulesGroupTextField.text
        }
    }

    fun applyNewConfiguration(settings: GeminioPluginSettings) {
        if (settings.config.configFilePath != configFilePathTextField.text) {
            settings.tryLoadFromConfigFile(configFilePathTextField.text)

            templatesRootDirPathTextField.text = settings.config.templatesRootDirPath
            modulesTemplatesRootDirPathTextField.text = settings.config.modulesTemplatesRootDirPath
            nameForNewGroupTextField.text = settings.config.groupsNames.forNewGroup
            nameForNewModulesGroupTextField.text = settings.config.groupsNames.forNewModulesGroup
        } else {
            settings.config = settings.config.copy(
                templatesRootDirPath = templatesRootDirPathTextField.text,
                modulesTemplatesRootDirPath = modulesTemplatesRootDirPathTextField.text,
                groupsNames = GeminioPluginConfig.GroupsNames(
                    forNewGroup = nameForNewGroupTextField.text,
                    forNewModulesGroup = nameForNewModulesGroupTextField.text
                ),
            )
        }
    }

}