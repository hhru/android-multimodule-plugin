package ru.hh.android.plugin.generator.steps

import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import ru.hh.android.plugin.core.model.psi.GradleDependency
import ru.hh.android.plugin.core.model.psi.GradleDependencyMode
import ru.hh.android.plugin.core.model.psi.GradleDependencyType
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.services.code_generator.BuildGradleModificationService


class AddFeatureModuleIntoDependenciesStep {

    fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyDependenciesBlock(appModuleItem.gradleModule, config)
        }
    }


    private fun modifyDependenciesBlock(module: Module, config: CreateModuleConfig) {
        module.project.service<BuildGradleModificationService>()
            .addGradleDependenciesIntoModule(
                module = module,
                gradleDependencies = listOf(
                    GradleDependency(
                        text = config.params.moduleName,
                        type = GradleDependencyType.MODULE,
                        mode = GradleDependencyMode.IMPLEMENTATION
                    )
                )
            )
    }

}