package ru.hh.android.plugin.config

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.extensions.EMPTY
import javax.swing.JComponent


class PluginConfigConfigurable(
        private val project: Project
) : SearchableConfigurable {

    companion object {
        private const val ID = "ru.hh.android.plugin.config.PluginConfigConfigurable"
        private const val DISPLAY_NAME = "HeadHunter plugin"
    }


    private val pluginConfig by lazy {
        PluginConfig.getInstance(project)
    }

    private var pluginConfigPropertiesEditor: PluginConfigEditor? = null


    override fun isModified(): Boolean {
        return pluginConfig.pathToPluginFolder != pluginConfigPropertiesEditor?.getPathToPluginFolder()
    }

    override fun getId(): String {
        return ID
    }

    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun apply() {
        pluginConfig.pathToPluginFolder = pluginConfigPropertiesEditor?.getPathToPluginFolder() ?: String.EMPTY
    }

    override fun createComponent(): JComponent? {
        return pluginConfigPropertiesEditor?.getRootPanel()
                ?: with(PluginConfigEditor(pluginConfig.pathToPluginFolder)) {
                    pluginConfigPropertiesEditor = this
                    getRootPanel()
                }
    }

    override fun disposeUIResources() {
        pluginConfigPropertiesEditor = null
    }
}
