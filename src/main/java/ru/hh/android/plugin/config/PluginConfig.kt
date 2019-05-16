package ru.hh.android.plugin.config

import com.android.tools.idea.util.toIoFile
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.extensions.EMPTY


@State(name = "ru.hh.android.plugin.config.PluginConfig")
class PluginConfig : PersistentStateComponent<PluginConfig> {

    companion object {
        fun getInstance(project: Project): PluginConfig {
            return ServiceManager.getService(project, PluginConfig::class.java).apply {
                if (pathToPluginFolder.isBlank()) {
                    val projectPath = project.guessProjectDir()!!.toIoFile().absolutePath
                    pathToPluginFolder = "$projectPath/${PluginConstants.DEFAULT_PLUGIN_CONFIG_FOLDER_NAME}"
                }
            }
        }
    }

    @Attribute
    var pathToPluginFolder: String = String.EMPTY


    override fun getState(): PluginConfig? {
        return this
    }

    override fun loadState(state: PluginConfig) {
        XmlSerializerUtil.copyBean(state, this)
    }
}