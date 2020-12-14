package ru.hh.android.plugin.generator.steps

import com.intellij.openapi.module.Module
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.code_modification.models.BuildGradleDependency
import ru.hh.plugins.code_modification.models.BuildGradleDependencyConfiguration


class AddFeatureModuleIntoDependenciesStep {

    fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyDependenciesBlock(appModuleItem.gradleModule, config)
        }
    }


    private fun modifyDependenciesBlock(module: Module, config: CreateModuleConfig) {
        BuildGradleModificationService.getInstance(module.project)
            .addDepsIntoModule(
                module = module,
                gradleDependencies = listOf(
                    BuildGradleDependency.Project(
                        configuration = BuildGradleDependencyConfiguration.IMPLEMENTATION,
                        value = config.params.moduleName
                    )
                )
            )
    }

}