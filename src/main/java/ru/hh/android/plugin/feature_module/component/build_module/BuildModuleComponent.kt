package ru.hh.android.plugin.feature_module.component.build_module

import com.intellij.openapi.components.ProjectComponent
import ru.hh.android.plugin.feature_module.component.build_module.factory.BuildModuleTaskFactory
import ru.hh.android.plugin.feature_module.component.build_module.task.BuildModuleTask
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.enums.CreateModuleBuildTaskType.*


class BuildModuleComponent(
        private val buildModuleTaskFactory: BuildModuleTaskFactory
) : ProjectComponent {

    fun buildNewFeatureModule(config: CreateModuleConfig) {
        getTasks(config).forEach { it.performBuildAction(config) }
    }


    private fun getTasks(config: CreateModuleConfig): List<BuildModuleTask> {
        return with(buildModuleTaskFactory) {
            mutableListOf<BuildModuleTask>().apply {
                this += createTask(DIRS_STRUCTURE_CREATION)
                this += createTask(MODULE_FILES_CREATION)
                this += createTask(SETTINGS_GRADLE_FILE_MODIFICATION)
                this += createTask(BUILD_GRADLE_DEPENDENCIES_BLOCK_MODIFICATION)
                this += createTask(TOOTHPICK_ANNOTATION_PROCESSING_CONFIG_MODIFICATION)

                if (config.mainParametersHolder.enableMoxy) {
                    this += createTask(MOXY_REFLECTOR_STUB_MODIFICATION)
                }
            }
        }
    }

}