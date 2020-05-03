package ru.hh.android.plugin.config

import com.intellij.openapi.components.service
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.config.view.PluginConfigEditor
import ru.hh.android.plugin.core.model.jira.JiraSettings
import ru.hh.android.plugin.extensions.EMPTY
import javax.swing.JComponent


/**
 * Wrapper for plugin configuration page.
 */
class PluginConfigConfigurable(
    private val project: Project
) : SearchableConfigurable {

    companion object {
        private const val ID = "ru.hh.android.plugin.config.PluginConfigConfigurable"
        private const val DISPLAY_NAME = "Geminio plugin"
    }


    private val pluginConfig by lazy {
        PluginConfig.getInstance(project)
    }

    private var pluginConfigPropertiesEditor: PluginConfigEditor? = null


    override fun isModified(): Boolean {
        return pluginConfigPropertiesEditor?.isModified() ?: false
    }

    override fun getId(): String {
        return ID
    }

    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun apply() {
        pluginConfigPropertiesEditor?.applyNewConfiguration(project, pluginConfig)
    }

    override fun createComponent(): JComponent? {
        pluginConfigPropertiesEditor = PluginConfigEditor.newInstance(pluginConfig, getJiraSettings(project))
        return pluginConfigPropertiesEditor?.createComponent()
    }


    override fun disposeUIResources() {
        pluginConfigPropertiesEditor = null
    }


    private fun getJiraSettings(project: Project): JiraSettings {
        val config = project.service<JiraSettingsConfig>()
        val configData = config.state

        return JiraSettings(
            hostName = config.key ?: String.EMPTY,
            username = configData?.userName ?: String.EMPTY,
            password = configData?.password ?: String.EMPTY
        )
    }

}
