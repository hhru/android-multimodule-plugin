package ru.hh.plugins.garcon.config.editor

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import ru.hh.plugins.garcon.config.GarconPluginConfig
import ru.hh.plugins.garcon.config.extensions.isNotFullyInitialized
import ru.hh.plugins.utils.yaml.YamlUtils

@Service(Service.Level.PROJECT)
@State(
    name = "ru.hh.plugins.garcon.config.editor.GarconPluginSettings",
    storages = [Storage("garcon_plugin_settings.xml")]
)
class GarconPluginSettings : PersistentStateComponent<GarconPluginSettings> {

    companion object {

        private const val DEFAULT_PATH_TO_CONFIG_FILE = "code-cookbook/templates/garcon/garcon_config.yaml"

        fun getInstance(project: Project): GarconPluginSettings {
            return project.service<GarconPluginSettings>().let { settings ->
                if (project.isDefault.not() && settings.config.isNotFullyInitialized()) {
                    YamlUtils.tryLoadFromConfigFile<GarconPluginConfig>(DEFAULT_PATH_TO_CONFIG_FILE).onSuccess {
                        settings.config = it
                    }
                }

                settings
            }
        }

        fun getConfig(project: Project): GarconPluginConfig {
            return getInstance(project).config
        }
    }

    var config: GarconPluginConfig = GarconPluginConfig()

    override fun getState(): GarconPluginSettings {
        return this
    }

    override fun loadState(state: GarconPluginSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
