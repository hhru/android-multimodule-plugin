package ru.hh.android.plugins.garcon.config.view

import com.intellij.openapi.project.Project
import com.intellij.refactoring.move.moveClassesOrPackages.DestinationFolderComboBox
import com.intellij.ui.ReferenceEditorComboWithBrowseButton
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import ru.hh.android.plugins.garcon.Constants
import ru.hh.android.plugins.garcon.config.PluginConfig
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.extensions.base_types.packageToPsiDirectory
import ru.hh.android.plugins.garcon.extensions.getTargetFilePath
import ru.hh.android.plugins.garcon.extensions.layout.targetFolderComboBox
import ru.hh.android.plugins.garcon.extensions.layout.targetPackageComboBox
import ru.hh.android.plugins.garcon.utils.GarconBundle
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JTextField


class PluginConfigEditor(
    private val initialPluginFolderDirPath: String,
    private val initialTargetPackageName: String,
    private val initialTargetFolderPath: String,
    private val initialEnableDebugMode: Boolean
) {

    companion object {

        fun newInstance(pluginConfig: PluginConfig): PluginConfigEditor {
            return PluginConfigEditor(
                initialPluginFolderDirPath = pluginConfig.pluginFolderDirPath,
                initialTargetPackageName = pluginConfig.defaultTargetPackageName,
                initialTargetFolderPath = pluginConfig.defaultTargetFolderPath,
                initialEnableDebugMode = pluginConfig.enableDebugMode
            )
        }

    }


    private lateinit var pluginFolderDirPathTextField: JTextField
    private lateinit var defaultPackageComboBox: ReferenceEditorComboWithBrowseButton
    private lateinit var defaultTargetDirectoryComboBox: DestinationFolderComboBox
    private lateinit var enableDebugModeCheckBox: JCheckBox


    @Suppress("UnstableApiUsage")
    fun createComponent(project: Project): JComponent? {
        return panel {
            titledRow(GarconBundle.message("garcon.config_editor.plugin_folder")) {
                row {
                    pluginFolderDirPathTextField = JTextField(initialPluginFolderDirPath)
                    pluginFolderDirPathTextField(CCFlags.growX)
                }
            }

            titledRow(GarconBundle.message("garcon.config_editor.default_target_package")) {
                row {
                    defaultPackageComboBox = targetPackageComboBox(
                        project = project,
                        recentPackageKey = Constants.SCREEN_PAGE_OBJECT_TARGET_PACKAGE_RECENT_KEY,
                        initialText = initialTargetPackageName,
                        labelText = GarconBundle.message("garcon.config_editor.default_target_package.label")
                    )
                    defaultPackageComboBox(CCFlags.growX)
                }
                row {
                    defaultTargetDirectoryComboBox = targetFolderComboBox(
                        project = project,
                        targetPackageComboBox = defaultPackageComboBox,
                        initialPsiDirectory = initialTargetPackageName
                            .packageToPsiDirectory(project = project, withPath = initialTargetFolderPath)
                    )
                    defaultTargetDirectoryComboBox(CCFlags.growX)
                }
            }

            titledRow(GarconBundle.message("garcon.config_editor.debug_mode")) {
                row {
                    enableDebugModeCheckBox = checkBox(
                        text = GarconBundle.message("garcon.config_editor.enable_debug_mode"),
                        isSelected = initialEnableDebugMode
                    )
                }
            }
        }
    }

    fun isModified(project: Project, pluginConfig: PluginConfig): Boolean {
        return pluginConfig.pluginFolderDirPath != pluginFolderDirPathTextField.text
                || pluginConfig.defaultTargetPackageName != defaultPackageComboBox.text
                || pluginConfig.defaultTargetFolderPath != getTargetDirectoryPath(project)
                || pluginConfig.enableDebugMode != enableDebugModeCheckBox.isSelected
    }

    fun applyNewConfiguration(project: Project, pluginConfig: PluginConfig) {
        pluginConfig.pluginFolderDirPath = pluginFolderDirPathTextField.text
        pluginConfig.defaultTargetPackageName = defaultPackageComboBox.text
        pluginConfig.defaultTargetFolderPath = getTargetDirectoryPath(project)
        pluginConfig.enableDebugMode = enableDebugModeCheckBox.isSelected
    }


    private fun getTargetDirectoryPath(project: Project): String {
        return defaultTargetDirectoryComboBox.getTargetFilePath(
            project = project,
            targetPackageName = defaultPackageComboBox.text
        ) ?: String.EMPTY
    }

}