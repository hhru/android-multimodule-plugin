package ru.hh.android.plugin.feature_module.component.build_module.task

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.extensions.createBreakLineElement
import ru.hh.android.plugin.feature_module.extensions.createModuleDependencyExpression
import ru.hh.android.plugin.feature_module.extensions.findPsiFileByName
import ru.hh.android.plugin.feature_module.extensions.firstChildWithStartText
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig


class BuildGradleDependenciesBlockModificationTask(
        project: Project,
        logger: PluginLogger
) : BuildModuleTask("Modify build.gradle dependencies block", project, logger) {

    companion object {
        private const val BUILD_GRADLE_FILE_NAME = "build.gradle"

        private const val DEPENDENCIES_BLOCK_NAME = "dependencies"
    }


    override fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyDependenciesBlock(appModuleItem.gradleModule, config)
        }
    }


    private fun modifyDependenciesBlock(module: Module, config: CreateModuleConfig) {
        val buildGradlePsiFile = module.findPsiFileByName(BUILD_GRADLE_FILE_NAME)

        if (buildGradlePsiFile == null) {
            logger.log("There is no $BUILD_GRADLE_FILE_NAME in ${module.name} module!")
            return
        }

        val dependenciesClosableBlock = buildGradlePsiFile
                .firstChildWithStartText(DEPENDENCIES_BLOCK_NAME)
                ?.lastChild

        if (dependenciesClosableBlock == null) {
            logger.log("There is no $DEPENDENCIES_BLOCK_NAME block in $BUILD_GRADLE_FILE_NAME!")
            return
        }

        with(dependenciesClosableBlock) {
            val factory = GroovyPsiElementFactory.getInstance(buildGradlePsiFile.project)
            val moduleName = config.mainParametersHolder.moduleName

            addBefore(factory.createModuleDependencyExpression(moduleName), lastChild)
            addBefore(factory.createBreakLineElement(), lastChild)
        }
    }

}