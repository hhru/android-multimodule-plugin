package ru.hh.plugins.geminio.config.editor

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import ru.hh.plugins.geminio.config.GeminioPluginConfig
import ru.hh.plugins.geminio.config.extensions.isNotFullyInitialized
import ru.hh.plugins.utils.yaml.YamlUtils

@State(
    name = "ru.hh.plugins.geminio.config.editor.GeminioPluginConfig",
    storages = [Storage("geminio_plugin_settings.xml")]
)
class GeminioPluginSettings : PersistentStateComponent<GeminioPluginSettings> {

    companion object {

        private const val DEFAULT_PATH_TO_CONFIG_FILE = "code-cookbook/templates/geminio/geminio_config.yaml"

        fun getInstance(project: Project): GeminioPluginSettings {
            return project.service<GeminioPluginSettings>().let { settings ->
                if (project.isDefault.not() && settings.config.isNotFullyInitialized()) {
                    settings.tryLoadFromConfigFile(DEFAULT_PATH_TO_CONFIG_FILE)
                }

                settings
            }
        }

        fun getConfig(project: Project): GeminioPluginConfig {
            return getInstance(project).config
        }
    }

    var config: GeminioPluginConfig = GeminioPluginConfig()

    override fun getState(): GeminioPluginSettings {
        return this
    }

    override fun loadState(state: GeminioPluginSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun tryLoadFromConfigFile(configFilePath: String) {
        YamlUtils.loadFromConfigFile<GeminioPluginConfig>(
            configFilePath = configFilePath,
            onError = {
                // todo
            }
        )?.let { configFromYaml ->
            this.config = configFromYaml.copy(
                configFilePath = configFilePath,
            )
        }
    }
}
