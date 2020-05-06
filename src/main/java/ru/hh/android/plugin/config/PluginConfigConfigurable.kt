package ru.hh.android.plugin.config

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.config.view.PluginConfigEditor
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
        pluginConfigPropertiesEditor = PluginConfigEditor.newInstance(pluginConfig)
        return pluginConfigPropertiesEditor?.createComponent()
    }


    override fun disposeUIResources() {
        pluginConfigPropertiesEditor = null
    }

}
