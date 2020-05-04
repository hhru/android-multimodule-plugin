package ru.hh.android.plugin.config.view

import com.intellij.credentialStore.Credentials
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.GrowPolicy
import com.intellij.ui.layout.panel
import ru.hh.android.plugin.config.JiraSettingsConfig
import ru.hh.android.plugin.config.PluginConfig
import ru.hh.android.plugin.core.model.jira.JiraDevelopmentTeam
import ru.hh.android.plugin.core.model.jira.JiraSettings
import ru.hh.android.plugin.utils.PluginBundle.message
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
                initialEnableDebugMode = pluginConfig.enableDebugMode,
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
            titledRow(message("antiroutine.config_editor.plugin_folder")) {
                row {
                    pluginFolderDirPathTextField = JTextField(initialPluginFolderDirPath)
                    pluginFolderDirPathTextField(CCFlags.growX)
                }
            }

            titledRow(message("antiroutine.config_editor.debug_mode")) {
                row {
                    enableDebugModeCheckBox = checkBox(
                        text = message("antiroutine.config_editor.enable_debug_mode"),
                        isSelected = initialEnableDebugMode
                    )
                }
            }

            titledRow(message("antiroutine.config_editor.jira_title")) {
                row(message("antiroutine.config_editor.jira_host")) {
                    jiraHostNameTextField = JTextField(initialJiraHostName)
                    jiraHostNameTextField()
                }
                row(message("antiroutine.config_editor.jira_username")) {
                    jiraUsernameTextField = JTextField(initialJiraUsername)
                    jiraUsernameTextField()
                }
                row(message("antiroutine.config_editor.jira_password")) {
                    jiraPasswordTextField = JPasswordField(initialJiraPassword.toString())
                    jiraPasswordTextField()
                }
            }

            titledRow(message("antiroutine.config_editor.jira_development_team")) {
                row {
                    jiraDevelopmentTeamComboBoxModel = CollectionComboBoxModel(JiraDevelopmentTeam.values().map { it.comboBoxLabel })
                    jiraDevelopmentTeamComboBoxModel.selectedItem = initialJiraDevelopmentTeam.comboBoxLabel
                    cell {
                        comboBox(
                            model = jiraDevelopmentTeamComboBoxModel,
                            getter = { jiraDevelopmentTeamComboBoxModel.selected },
                            setter = { /* do nothing */ },
                            growPolicy = GrowPolicy.SHORT_TEXT,
                            renderer = null
                        ).also { it.component(CCFlags.growX) }
                    }
                }
            }
        }
    }

    fun isModified(): Boolean {
        return initialPluginFolderDirPath != pluginFolderDirPathTextField.text
            || initialEnableDebugMode != enableDebugModeCheckBox.isSelected
            || initialJiraHostName != jiraHostNameTextField.text
            || initialJiraUsername != jiraUsernameTextField.text
            || initialJiraPassword != jiraPasswordTextField.text
            || initialJiraDevelopmentTeam != JiraDevelopmentTeam.fromLabel(jiraDevelopmentTeamComboBoxModel.selected.orEmpty())
    }

    fun applyNewConfiguration(project: Project, pluginConfig: PluginConfig) {
        pluginConfig.pluginFolderDirPath = pluginFolderDirPathTextField.text
        pluginConfig.enableDebugMode = enableDebugModeCheckBox.isSelected
        pluginConfig.jiraDevelopmentTeam = JiraDevelopmentTeam.fromLabel(jiraDevelopmentTeamComboBoxModel.selected.orEmpty())

        with(JiraSettingsConfig.getInstance(project)) {
            writeHostname(jiraHostNameTextField.text)
            loadState(Credentials(jiraUsernameTextField.text, jiraPasswordTextField.text))
        }
    }

}