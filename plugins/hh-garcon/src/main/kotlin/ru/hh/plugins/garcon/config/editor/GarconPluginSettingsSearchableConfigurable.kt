package ru.hh.plugins.garcon.config.editor

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class GarconPluginSettingsSearchableConfigurable(
    private val project: Project
) : SearchableConfigurable {

    companion object {
        private const val ID = "ru.hh.plugins.garcon.config.GarconPluginSettingsSearchableConfigurable"
        private const val DISPLAY_NAME = "Garcon plugin"
    }

    private val pluginConfig by lazy {
        GarconPluginSettings.getInstance(project)
    }

    private var pluginConfigPropertiesEditor: GarconPluginSettingsEditor? = null

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
        pluginConfigPropertiesEditor = GarconPluginSettingsEditor.newInstance(pluginConfig)
        return pluginConfigPropertiesEditor?.createComponent(project)
    }

    override fun disposeUIResources() {
        pluginConfigPropertiesEditor = null
    }
}
