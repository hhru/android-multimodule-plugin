package ru.hh.android.plugins.garcon.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import org.jetbrains.kotlin.idea.util.projectStructure.allModules
import ru.hh.android.plugins.garcon.Constants
import ru.hh.android.plugins.garcon.extensions.androidModulePackageName
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.extensions.base_types.packageToPsiDirectory
import ru.hh.android.plugins.garcon.extensions.isAppModule


@State(name = "ru.hh.android.plugins.garcon.config.PluginConfig")
class PluginConfig : PersistentStateComponent<PluginConfig> {

    companion object {

        fun getInstance(project: Project): PluginConfig {
            return ServiceManager.getService(project, PluginConfig::class.java).apply {
                if (project.isDefault.not() && pluginFolderDirPath.isBlank()) {
                    val projectPath = project.basePath
                    pluginFolderDirPath = "$projectPath/${Constants.DEFAULT_PLUGIN_CONFIG_FOLDER_NAME}"
                    defaultTargetPackageName = chooseDefaultPackageName(project)
                    defaultTargetFolderPath = chooseDefaultFolderPath(defaultTargetPackageName, project)
                    enableDebugMode = false
                }
            }
        }

    }

    @Attribute
    var pluginFolderDirPath: String = String.EMPTY

    @Attribute
    var defaultTargetPackageName: String = String.EMPTY

    @Attribute
    var defaultTargetFolderPath: String = String.EMPTY

    @Attribute
    var enableDebugMode: Boolean = false


    override fun getState(): PluginConfig? {
        return this
    }

    override fun loadState(state: PluginConfig) {
        XmlSerializerUtil.copyBean(state, this)
    }


    private fun chooseDefaultPackageName(project: Project): String {
        val firstAppModule = project.allModules().firstOrNull { it.isAppModule() }

        return firstAppModule?.androidModulePackageName ?: String.EMPTY
    }

    private fun chooseDefaultFolderPath(defaultPackage: String, project: Project): String {
        return defaultPackage.packageToPsiDirectory(project, String.EMPTY)?.virtualFile?.path ?: String.EMPTY
    }

}