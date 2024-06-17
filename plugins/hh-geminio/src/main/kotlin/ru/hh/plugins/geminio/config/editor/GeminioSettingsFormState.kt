package ru.hh.plugins.geminio.config.editor

import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import ru.hh.plugins.geminio.config.GeminioPluginConfig

internal class GeminioSettingsFormState(
    propertyGraph: PropertyGraph = PropertyGraph(),
    val configFilePath: ObservableMutableProperty<String> = propertyGraph.property(""),
    val templatesRootDirPath: ObservableMutableProperty<String> = propertyGraph.property(""),
    val modulesTemplatesRootDirPath: ObservableMutableProperty<String> = propertyGraph.property(""),
    val nameForNewGroup: ObservableMutableProperty<String> = propertyGraph.property(""),
    val nameForNewModulesGroup: ObservableMutableProperty<String> = propertyGraph.property(""),
    val isDebugEnabled: ObservableMutableProperty<Boolean> = propertyGraph.property(false),
) {

    constructor(config: GeminioPluginConfig) : this() {
        this.set(config)
    }

    fun set(config: GeminioPluginConfig) {
        configFilePath.set(config.configFilePath)
        templatesRootDirPath.set(config.templatesRootDirPath)
        modulesTemplatesRootDirPath.set(config.modulesTemplatesRootDirPath)
        nameForNewGroup.set(config.groupsNames.forNewGroup)
        nameForNewModulesGroup.set(config.groupsNames.forNewModulesGroup)
        isDebugEnabled.set(config.isDebugEnabled)
    }

    fun isModified(originalConfig: GeminioPluginConfig): Boolean {
        return !(
            originalConfig.configFilePath == this.configFilePath.get() &&
                originalConfig.templatesRootDirPath == this.templatesRootDirPath.get() &&
                originalConfig.modulesTemplatesRootDirPath == this.modulesTemplatesRootDirPath.get() &&
                originalConfig.groupsNames.forNewGroup == nameForNewGroup.get() &&
                originalConfig.groupsNames.forNewModulesGroup == nameForNewModulesGroup.get() &&
                originalConfig.isDebugEnabled == isDebugEnabled.get()
            )
    }

}
