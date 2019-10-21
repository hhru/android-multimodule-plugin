package ru.hh.android.plugin.generator.steps

import com.intellij.openapi.module.Module
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.android.plugin.extensions.createBreakLineElement
import ru.hh.android.plugin.extensions.createModuleDependencyExpression
import ru.hh.android.plugin.extensions.findPsiFileByName
import ru.hh.android.plugin.extensions.firstChildWithStartText
import ru.hh.android.plugin.model.CreateModuleConfig


class AddFeatureModuleIntoDependenciesStep {

    companion object {
        private const val BUILD_GRADLE_FILE_NAME = "build.gradle"

        private const val DEPENDENCIES_BLOCK_NAME = "dependencies"
    }


    fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyDependenciesBlock(appModuleItem.gradleModule, config)
        }
    }


    private fun modifyDependenciesBlock(module: Module, config: CreateModuleConfig) {
        val buildGradlePsiFile = module.findPsiFileByName(BUILD_GRADLE_FILE_NAME) ?: return

        val dependenciesClosableBlock = buildGradlePsiFile
                .firstChildWithStartText(DEPENDENCIES_BLOCK_NAME)
                ?.lastChild
                ?: return

        with(dependenciesClosableBlock) {
            val factory = GroovyPsiElementFactory.getInstance(buildGradlePsiFile.project)
            val moduleName = config.params.moduleName

            addBefore(factory.createModuleDependencyExpression(moduleName), lastChild)
            addBefore(factory.createBreakLineElement(), lastChild)
            CodeStyleManager.getInstance(module.project).reformat(dependenciesClosableBlock)
        }
    }

}