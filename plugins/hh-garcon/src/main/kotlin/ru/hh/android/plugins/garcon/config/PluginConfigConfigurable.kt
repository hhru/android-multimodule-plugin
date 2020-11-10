package ru.hh.android.plugins.garcon.config

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import ru.hh.android.plugins.garcon.config.view.PluginConfigEditor
import javax.swing.JComponent


class PluginConfigConfigurable(
    private val project: Project
) : SearchableConfigurable {

    companion object {
        private const val ID = "ru.hh.android.plugins.garcon.config.PluginConfigConfigurable"
        private const val DISPLAY_NAME = "Garcon plugin"
    }


    private val pluginConfig by lazy {
        PluginConfig.getInstance(project)
    }

    private var pluginConfigPropertiesEditor: PluginConfigEditor? = null


    override fun isModified(): Boolean {
        return pluginConfigPropertiesEditor?.isModified(project, pluginConfig) ?: false
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
        pluginConfigPropertiesEditor = PluginConfigEditor.newInstance(pluginConfig)
        return pluginConfigPropertiesEditor?.createComponent(project)
    }


    override fun disposeUIResources() {
        pluginConfigPropertiesEditor = null
    }

}