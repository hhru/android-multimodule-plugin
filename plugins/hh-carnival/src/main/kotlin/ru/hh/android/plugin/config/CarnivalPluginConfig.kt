package ru.hh.android.plugin.config

import com.android.tools.idea.util.toIoFile
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.core.model.jira.JiraDevelopmentTeam
import ru.hh.plugins.extensions.EMPTY

@Service(Service.Level.PROJECT)
@State(name = "ru.hh.android.plugin.config.PluginConfig")
class CarnivalPluginConfig : PersistentStateComponent<CarnivalPluginConfig> {

    companion object {
        fun getInstance(project: Project): CarnivalPluginConfig {
            return project.service<CarnivalPluginConfig>().apply {
                if (project.isDefault.not() && pluginFolderDirPath.isBlank()) {
                    val projectPath = project.guessProjectDir()!!.toIoFile().absolutePath
                    pluginFolderDirPath = "$projectPath/${PluginConstants.DEFAULT_PLUGIN_CONFIG_FOLDER_NAME}"
                }
            }
        }
    }

    @Attribute
    var pluginFolderDirPath: String = String.EMPTY

    @Attribute
    var isDebugEnabled: Boolean = false

    @Attribute
    var jiraDevelopmentTeam: JiraDevelopmentTeam = JiraDevelopmentTeam.MOBILE_CORE

    override fun getState(): CarnivalPluginConfig {
        return this
    }

    override fun loadState(state: CarnivalPluginConfig) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
