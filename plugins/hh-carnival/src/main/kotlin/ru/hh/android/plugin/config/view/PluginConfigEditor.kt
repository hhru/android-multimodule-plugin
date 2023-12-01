package ru.hh.android.plugin.config.view

import com.intellij.credentialStore.Credentials
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import ru.hh.android.plugin.config.JiraSettingsConfig
import ru.hh.android.plugin.config.PluginConfig
import ru.hh.android.plugin.core.model.jira.JiraDevelopmentTeam
import ru.hh.android.plugin.core.model.jira.JiraSettings
import ru.hh.plugins.logger.HHLogger
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPasswordField
import javax.swing.JTextField

/**
 * Editor page for plugin configuration
 */
class PluginConfigEditor(
    private val initialPluginFolderDirPath: String,
    private val initialEnableDebugMode: Boolean,
    private val initialJiraHostName: String,
    private val initialJiraUsername: String,
    private val initialJiraPassword: CharSequence,
    private val initialJiraDevelopmentTeam: JiraDevelopmentTeam
) {

    companion object {

        fun newInstance(pluginConfig: PluginConfig, jiraSettings: JiraSettings): PluginConfigEditor {
            return PluginConfigEditor(
                initialPluginFolderDirPath = pluginConfig.pluginFolderDirPath,
                initialEnableDebugMode = pluginConfig.isDebugEnabled,
                initialJiraHostName = jiraSettings.hostName,
                initialJiraUsername = jiraSettings.username,
                initialJiraPassword = jiraSettings.password,
                initialJiraDevelopmentTeam = pluginConfig.jiraDevelopmentTeam
            )
        }
    }

    private lateinit var pluginFolderDirPathTextField: JTextField
    private lateinit var enableDebugModeCheckBox: JCheckBox
    private lateinit var jiraHostNameTextField: JTextField
    private lateinit var jiraUsernameTextField: JTextField
    private lateinit var jiraPasswordTextField: JPasswordField
    private lateinit var jiraDevelopmentTeamComboBoxModel: CollectionComboBoxModel<String>

    @Suppress("UnstableApiUsage")
    fun createComponent(): JComponent? {
        return panel {
            titledRow("Path to plugin's folder") {
                row {
                    pluginFolderDirPathTextField = JTextField(initialPluginFolderDirPath)
                    pluginFolderDirPathTextField(CCFlags.growX)
                }
            }

            titledRow("Debug mode settings") {
                row {
                    enableDebugModeCheckBox = checkBox(
                        text = "Enable debug mode",
                        isSelected = initialEnableDebugMode
                    ).component
                }
            }

            titledRow("Enter your JIRA credentials:") {
                row("Host name:") {
                    jiraHostNameTextField = JTextField(initialJiraHostName)
                    jiraHostNameTextField()
                }
                row("Username:") {
                    jiraUsernameTextField = JTextField(initialJiraUsername)
                    jiraUsernameTextField()
                }
                row("Password:") {
                    jiraPasswordTextField = JPasswordField(initialJiraPassword.toString())
                    jiraPasswordTextField()
                }
            }

            titledRow("Choose your JIRA development team:") {
                row {
                    jiraDevelopmentTeamComboBoxModel =
                        CollectionComboBoxModel(JiraDevelopmentTeam.values().map { it.comboBoxLabel })
                    jiraDevelopmentTeamComboBoxModel.selectedItem = initialJiraDevelopmentTeam.comboBoxLabel
                    cell {
                        comboBox(
                            model = jiraDevelopmentTeamComboBoxModel,
                            getter = { jiraDevelopmentTeamComboBoxModel.selected },
                            setter = { /* do nothing */ },
                            renderer = null
                        ).also { it.component(CCFlags.growX) }
                    }
                }
            }
        }
    }

    fun isModified(): Boolean {
        return initialPluginFolderDirPath != pluginFolderDirPathTextField.text ||
            initialEnableDebugMode != enableDebugModeCheckBox.isSelected ||
            initialJiraHostName != jiraHostNameTextField.text ||
            initialJiraUsername != jiraUsernameTextField.text ||
            initialJiraPassword != jiraPasswordTextField.text ||
            initialJiraDevelopmentTeam != JiraDevelopmentTeam.fromLabel(
                jiraDevelopmentTeamComboBoxModel.selected.orEmpty()
            )
    }

    fun applyNewConfiguration(project: Project, pluginConfig: PluginConfig) {
        pluginConfig.pluginFolderDirPath = pluginFolderDirPathTextField.text
        pluginConfig.isDebugEnabled = enableDebugModeCheckBox.isSelected
        pluginConfig.jiraDevelopmentTeam =
            JiraDevelopmentTeam.fromLabel(jiraDevelopmentTeamComboBoxModel.selected.orEmpty())

        with(JiraSettingsConfig.getInstance(project)) {
            writeHostname(jiraHostNameTextField.text)
            loadState(Credentials(jiraUsernameTextField.text, jiraPasswordTextField.text))
        }

        HHLogger.enableDebug(pluginConfig.isDebugEnabled)
    }
}
