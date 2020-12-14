package ru.hh.plugins.code_modification

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.impl.GroovyFileImpl
import ru.hh.plugins.code_modification.extensions.psi.createBuildGradleDependencyElement
import ru.hh.plugins.code_modification.models.BuildGradleDependency
import ru.hh.plugins.extensions.openapi.findPsiFileByName
import ru.hh.plugins.extensions.psi.reformatWithCodeStyle


// TODO add modification for Kotlin build.gradle.kts

/**
 * Service for adding dependencies into build.gradle files.
 */
@Service
class BuildGradleModificationService(
    private val project: Project
) {

    companion object {
        private const val COMMAND_NAME = "BuildGradleModificationCommand"

        private const val BUILD_GRADLE_FILENAME = "build.gradle"
        private const val DEPENDENCIES_BLOCK_NAME = "dependencies"


        fun getInstance(project: Project): BuildGradleModificationService = project.service()
    }


    fun addDepsIntoModule(
        module: Module,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            internalAddGradleDependencies(module, gradleDependencies)
        }
    }

    fun addDepsIntoFile(
        psiFile: PsiFile,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            internalModifyPsiFile(psiFile, gradleDependencies)
        }
    }

    fun addDepsInModuleDirectory(
        rootDir: PsiDirectory?,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            val buildGradleFile = rootDir?.findFile(BUILD_GRADLE_FILENAME) as? GroovyFileImpl
                ?: return@wrapInCommand

            internalModifyPsiFile(buildGradleFile, gradleDependencies)
        }
    }


    private inline fun wrapInCommand(isInWriteCommand: Boolean, crossinline action: () -> Unit) {
        if (isInWriteCommand) {
            action.invoke()
        } else {
            project.executeWriteCommand(COMMAND_NAME) {
                action.invoke()
            }
        }
    }

    private fun internalModifyPsiFile(psiFile: PsiFile, gradleDependencies: List<BuildGradleDependency>) {
        if (psiFile is GroovyFileImpl && psiFile.name == BUILD_GRADLE_FILENAME) {
            modifyDependenciesBlock(psiFile, gradleDependencies)
        }
    }

    private fun internalAddGradleDependencies(module: Module, gradleDependencies: List<BuildGradleDependency>) {
        val buildGradlePsiFile = module.findPsiFileByName(BUILD_GRADLE_FILENAME) as? GroovyFileImpl
            ?: throw IllegalStateException("Can't find \"$BUILD_GRADLE_FILENAME\" in \"${module.name}\"")

        modifyDependenciesBlock(buildGradlePsiFile, gradleDependencies)
    }

    private fun modifyDependenciesBlock(
        buildGradlePsiFile: GroovyFileImpl,
        gradleDependencies: List<BuildGradleDependency>
    ) {
        val dependenciesClosableBlock = buildGradlePsiFile.findChildrenByClass(GrMethodCall::class.java)
            .firstOrNull { it.text.startsWith(DEPENDENCIES_BLOCK_NAME) }
            ?.findDescendantOfType<GrClosableBlock>()
            ?: return

        val existingDependencies = dependenciesClosableBlock.children.filterIsInstance<GrApplicationStatement>()
            .mapTo(mutableSetOf()) { dependency ->
                dependency.argumentList.text
                    .removePrefix("project(")
                    .removeSuffix(")")
                    .removeSurrounding("'")
                    .removeSurrounding("\"")
            }

        val factory = GroovyPsiElementFactory.getInstance(buildGradlePsiFile.project)
        gradleDependencies.forEach { dependency ->
            if (existingDependencies.contains(dependency.value).not()) {
                val element = factory.createBuildGradleDependencyElement(dependency)
                dependenciesClosableBlock.addBefore(element, dependenciesClosableBlock.rBrace)
            }
        }

        buildGradlePsiFile.reformatWithCodeStyle()
    }

}