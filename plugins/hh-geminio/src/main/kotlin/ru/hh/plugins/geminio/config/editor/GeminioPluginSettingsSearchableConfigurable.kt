package ru.hh.plugins.geminio.config.editor

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class GeminioPluginSettingsSearchableConfigurable(
    private val project: Project
) : SearchableConfigurable {

    companion object {
        private const val ID = "ru.hh.plugins.geminio.config.editor.GeminioPluginSettingsSearchableConfigurable"
        private const val DISPLAY_NAME = "Geminio plugin"
    }

    private val pluginConfig by lazy {
        GeminioPluginSettings.getInstance(project)
    }

    private var pluginConfigPropertiesEditor: GeminioPluginSettingsEditor? = null

    override fun isModified(): Boolean {
        return pluginConfigPropertiesEditor?.isModified(pluginConfig) ?: false
    }

    override fun getId(): String {
        return ID
    }

    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun apply() {
        pluginConfigPropertiesEditor?.applyNewConfiguration(pluginConfig)
    }

    override fun createComponent(): JComponent? {
        pluginConfigPropertiesEditor = GeminioPluginSettingsEditor.newInstance(pluginConfig)
        return pluginConfigPropertiesEditor?.createComponent(project)
    }

    override fun disposeUIResources() {
        pluginConfigPropertiesEditor = null
    }
}
