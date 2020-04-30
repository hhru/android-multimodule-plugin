package ru.hh.android.plugin.config.view

import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import ru.hh.android.plugin.config.PluginConfig
import ru.hh.android.plugin.utils.PluginBundle
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JTextField


/**
 * Editor page for plugin configuration
 */
class PluginConfigEditor(
        private val initialPluginFolderDirPath: String,
        private val initialEnableDebugMode: Boolean
) {

    companion object {

        fun newInstance(pluginConfig: PluginConfig): PluginConfigEditor {
            return PluginConfigEditor(
                    initialPluginFolderDirPath = pluginConfig.pluginFolderDirPath,
                    initialEnableDebugMode = pluginConfig.enableDebugMode
            )
        }

    }


    private lateinit var pluginFolderDirPathTextField: JTextField
    private lateinit var enableDebugModeCheckBox: JCheckBox


    @Suppress("UnstableApiUsage")
    fun createComponent(): JComponent? {
        return panel {
            titledRow(PluginBundle.message("geminio.config_editor.plugin_folder")) {
                row {
                    pluginFolderDirPathTextField = JTextField(initialPluginFolderDirPath)
                    pluginFolderDirPathTextField(CCFlags.growX)
                }
            }

            titledRow(PluginBundle.message("geminio.config_editor.debug_mode")) {
                row {
                    enableDebugModeCheckBox = checkBox(
                            text = PluginBundle.message("geminio.config_editor.enable_debug_mode"),
                            isSelected = initialEnableDebugMode
                    )
                }
            }
        }
    }

    fun isModified(pluginConfig: PluginConfig): Boolean {
        return pluginConfig.pluginFolderDirPath != pluginFolderDirPathTextField.text
                || pluginConfig.enableDebugMode != enableDebugModeCheckBox.isSelected
    }

    fun applyNewConfiguration(pluginConfig: PluginConfig) {
        pluginConfig.pluginFolderDirPath = pluginFolderDirPathTextField.text
        pluginConfig.enableDebugMode = enableDebugModeCheckBox.isSelected
    }

}