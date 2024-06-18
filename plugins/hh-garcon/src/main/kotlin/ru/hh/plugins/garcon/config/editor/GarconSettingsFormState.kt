package ru.hh.plugins.garcon.config.editor

import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import ru.hh.plugins.garcon.config.GarconPluginConfig

internal class GarconSettingsFormState(
    propertyGraph: PropertyGraph = PropertyGraph(),
    val configFilePath: ObservableMutableProperty<String> = propertyGraph.property(""),
    val enableDebugMode: ObservableMutableProperty<Boolean> = propertyGraph.property(false),
    val screenPageObjectTemplatePath: ObservableMutableProperty<String> = propertyGraph.property(""),
    val rvItemPageObjectTemplatePath: ObservableMutableProperty<String> = propertyGraph.property(""),
) {

    constructor(config: GarconPluginConfig) : this() {
        this.set(config)
    }

    fun set(config: GarconPluginConfig) {
        configFilePath.set(config.configFilePath)
        enableDebugMode.set(config.isDebugEnabled)
        screenPageObjectTemplatePath.set(config.templatesPaths.screenPageObjectTemplatePath)
        rvItemPageObjectTemplatePath.set(config.templatesPaths.rvItemPageObjectTemplatePath)
    }

    fun isModified(originalConfig: GarconPluginConfig): Boolean {
        return !(
            originalConfig.configFilePath == this.configFilePath.get() &&
                originalConfig.isDebugEnabled == enableDebugMode.get() &&
                originalConfig.templatesPaths.screenPageObjectTemplatePath == this.screenPageObjectTemplatePath.get() &&
                originalConfig.templatesPaths.rvItemPageObjectTemplatePath == this.rvItemPageObjectTemplatePath.get()
            )
    }

}
