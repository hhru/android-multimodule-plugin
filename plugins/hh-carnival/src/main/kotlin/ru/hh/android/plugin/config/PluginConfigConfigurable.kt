package ru.hh.android.plugin.config

import com.intellij.credentialStore.Credentials
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toMutableProperty
import com.intellij.ui.dsl.builder.toNullableProperty
import ru.hh.android.plugin.config.CarnivalSettingsFormState.ClearableCharArray
import ru.hh.android.plugin.core.model.jira.JiraDevelopmentTeam
import ru.hh.plugins.extensions.layout.listCellRenderer
import ru.hh.plugins.extensions.layout.passwordFieldCompat
import ru.hh.plugins.logger.HHLogger

/**
 * Wrapper for plugin configuration page.
 */
class PluginConfigConfigurable(
    private val project: Project
) : BoundSearchableConfigurable(
    displayName = DISPLAY_NAME,
    helpTopic = DISPLAY_NAME,
    _id = ID
) {

    companion object {
        private const val ID = "ru.hh.android.plugin.config.PluginConfigConfigurable"
        private const val DISPLAY_NAME = "Geminio plugin"
        private const val JIRA_CREDENTIALS_UI_GROUP = "JiraCredentials"
    }

    private val pluginConfig by lazy {
        CarnivalPluginConfig.getInstance(project)
    }
    private val jiraConfig
        get() = JiraSettingsConfig.getInstance(project)

    private var formState: CarnivalSettingsFormState? = null

    override fun apply() {
        super.apply()
        applyNewConfiguration()
    }

    @Suppress("detekt.LongMethod")
    override fun createPanel(): DialogPanel {
        val formState = CarnivalSettingsFormState(
            pluginConfig = pluginConfig,
            jiraSettings = jiraConfig.getJiraSettings()
        )
        this.formState = formState
        return panel {
            groupRowsRange("Path to Plugin Folder") {
                row {
                    textField()
                        .bindText(formState::pluginFolderDirPath)
                        .resizableColumn()
                        .align(Align.FILL)
                }
            }
            groupRowsRange("Debug Settings") {
                row {
                    checkBox("Enable debug mode")
                        .bindSelected(formState::enableDebugMode)
                }
            }
            groupRowsRange("JIRA Credentials") {
                row {
                    label("Enter your JIRA credentials:")
                }
                row("Host name:") {
                    textField()
                        .bindText(formState::jiraHostname)
                        .columns(COLUMNS_LARGE)
                        .widthGroup(JIRA_CREDENTIALS_UI_GROUP)
                }
                row("Username:") {
                    textField()
                        .bindText(formState::jiraUsername)
                        .columns(COLUMNS_LARGE)
                        .widthGroup(JIRA_CREDENTIALS_UI_GROUP)
                }
                row("Password:") {
                    passwordFieldCompat()
                        .bind(
                            { field -> ClearableCharArray(*field.password) },
                            { field, value -> field.text = value.toString() },
                            formState::jiraPassword.toMutableProperty()
                        )
                        .columns(COLUMNS_LARGE)
                        .widthGroup(JIRA_CREDENTIALS_UI_GROUP)
                }
            }
            groupRowsRange("JIRA Development Team") {
                row {
                    comboBox(
                        model = EnumComboBoxModel(JiraDevelopmentTeam::class.java),
                        renderer = listCellRenderer {
                            setText(it.comboBoxLabel)
                        }
                    )
                        .bindItem(formState::jiraDevelopmentTeam.toNullableProperty())
                        .columns(COLUMNS_LARGE)
                        .label("Choose your JIRA development team:", LabelPosition.TOP)
                }
            }
        }
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
        formState?.jiraPassword?.clear()
        formState = null
    }

    private fun applyNewConfiguration() {
        val formState = checkNotNull(this.formState)
        with(pluginConfig) {
            pluginFolderDirPath = formState.pluginFolderDirPath
            isDebugEnabled = formState.enableDebugMode
            jiraDevelopmentTeam = formState.jiraDevelopmentTeam
        }

        with(jiraConfig) {
            writeHostname(formState.jiraHostname)
            loadState(Credentials(formState.jiraUsername, formState.jiraPassword.charsNewCopy()))
        }

        HHLogger.enableDebug(pluginConfig.isDebugEnabled)
    }
}
