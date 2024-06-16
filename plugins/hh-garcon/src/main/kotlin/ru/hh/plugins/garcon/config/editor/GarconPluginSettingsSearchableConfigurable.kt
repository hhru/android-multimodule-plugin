package ru.hh.plugins.garcon.config.editor

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.COLUMNS_MEDIUM
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import ru.hh.plugins.PluginsConstants
import ru.hh.plugins.garcon.config.GarconPluginConfig
import ru.hh.plugins.garcon.config.extensions.copyFromFormState
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.utils.yaml.YamlUtils

class GarconPluginSettingsSearchableConfigurable(
    private val project: Project
) : BoundSearchableConfigurable(
    displayName = DISPLAY_NAME,
    helpTopic = DISPLAY_NAME,
    _id = ID
) {

    private companion object {
        const val ID = "ru.hh.plugins.garcon.config.GarconPluginSettingsSearchableConfigurable"
        const val DISPLAY_NAME = "Garcon Plugin"
    }

    private val pluginConfig by lazy {
        GarconPluginSettings.getInstance(project)
    }

    private var formState: GarconSettingsFormState? = null

    override fun isModified(): Boolean {
        return super.isModified() || formState?.isModified(pluginConfig.config) ?: false
    }

    override fun apply() {
        super.apply()
        applyNewConfiguration()
        HHLogger.enableDebug(pluginConfig.config.isDebugEnabled)
    }

    override fun createPanel(): DialogPanel {
        val formState = GarconSettingsFormState(pluginConfig.config)
        this.formState = formState
        return panel {
            row("Config File:") {
                textFieldWithBrowseButton(
                    browseDialogTitle = "Config File Path",
                    project = project,
                    fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(
                        PluginsConstants.YAML_FILES_FILTER_EXTENSION
                    ),
                    fileChosen = { chosenFile ->
                        chosenFile.presentableUrl.also {
                            applyConfigurationFromFile(it)
                        }
                    }
                )
                    .columns(COLUMNS_LARGE)
                    .bindText(formState.configFilePath)
            }
            groupRowsRange("Templates Paths") {
                row("") {
                    textField()
                        .columns(COLUMNS_MEDIUM)
                        .bindText(formState.screenPageObjectTemplatePath)
                }
                row("") {
                    textField()
                        .columns(COLUMNS_MEDIUM)
                        .bindText(formState.rvItemPageObjectTemplatePath)
                }
            }
            row {
                checkBox("Enable debug mode")
                    .bindSelected(formState.enableDebugMode)
            }
        }
    }

    override fun reset() {
        super.reset()
        formState?.set(pluginConfig.config)
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
        formState = null
    }

    private fun applyNewConfiguration() {
        val formState = checkNotNull(formState)
        val newConfigPath = formState.configFilePath.get()

        if (pluginConfig.config.configFilePath != newConfigPath) {
            applyConfigurationFromFile(newConfigPath)
        }
        pluginConfig.config = pluginConfig.config.copyFromFormState(formState)
    }

    private fun applyConfigurationFromFile(filePath: String) {
        val newConfig = YamlUtils.tryLoadFromConfigFile<GarconPluginConfig>(filePath).getOrNull() ?: return
        formState?.set(newConfig)
    }
}
