package ru.hh.android.plugin.services.code_generator

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.executeCommand
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression
import ru.hh.android.plugin.core.model.psi.GradleDependency
import ru.hh.android.plugin.exceptions.NoFileFoundException
import ru.hh.android.plugin.extensions.findPsiFileByName
import ru.hh.android.plugin.extensions.psi.groovy.getBreakLineElement
import ru.hh.android.plugin.extensions.psi.groovy.getGradleDependencyExpression
import ru.hh.android.plugin.utils.PluginBundle.message
import ru.hh.android.plugin.utils.reformatWithCodeStyle


/**
 * Modification service for build.gradle file
 */
@Service
class BuildGradleModificationService {

    companion object {
        const val BUILD_GRADLE_FILENAME = "build.gradle"

        private const val DEPENDENCIES_BLOCK_NAME = "dependencies"

        fun getInstance(project: Project): BuildGradleModificationService = project.service()
    }


    fun addGradleDependenciesIntoModule(module: Module, gradleDependencies: List<GradleDependency>) {
        executeCommand {
            runWriteAction {
                internalAddGradleDependencies(module, gradleDependencies)
            }
        }
    }

    fun addGradleDependenciesIntoBuildGradleFile(psiFile: PsiFile, gradleDependencies: List<GradleDependency>) {
        executeCommand {
            runWriteAction {
                if (psiFile is GroovyFile && psiFile.name == BUILD_GRADLE_FILENAME) {
                    modifyDependenciesBlock(psiFile, gradleDependencies)
                }
            }
        }
    }


    private fun internalAddGradleDependencies(module: Module, gradleDependencies: List<GradleDependency>) {
        val buildGradlePsiFile = module.findPsiFileByName(BUILD_GRADLE_FILENAME)
            ?: throw NoFileFoundException(
                message("geminio.errors.common.no_file_found_in_module.0.1", BUILD_GRADLE_FILENAME, module.name)
            )

        modifyDependenciesBlock(buildGradlePsiFile, gradleDependencies)
    }

    private fun modifyDependenciesBlock(buildGradlePsiFile: PsiFile, gradleDependencies: List<GradleDependency>) {
        buildGradlePsiFile.findDescendantOfType<GrMethodCallExpression> { callExpression ->
            callExpression.text.startsWith(DEPENDENCIES_BLOCK_NAME) && callExpression.parent == buildGradlePsiFile
        }
            ?.findDescendantOfType<GrClosableBlock>()
            ?.let { dependenciesBlock ->
                val factory = GroovyPsiElementFactory.getInstance(dependenciesBlock.project)

                gradleDependencies.forEach { gradleDependency ->
                    val dependencyExpression = factory.getGradleDependencyExpression(gradleDependency)

                    dependenciesBlock.addBefore(dependencyExpression, dependenciesBlock.rBrace)
                    dependenciesBlock.addBefore(factory.getBreakLineElement(), dependenciesBlock.rBrace)
                }

                dependenciesBlock.reformatWithCodeStyle()
            }
    }

}