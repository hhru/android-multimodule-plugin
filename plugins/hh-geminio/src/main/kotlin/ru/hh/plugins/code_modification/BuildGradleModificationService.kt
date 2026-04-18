package ru.hh.plugins.code_modification

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.plugins.groovy.lang.psi.impl.GroovyFileImpl
import ru.hh.plugins.code_modification.GradleConstants.BUILD_GRADLE_FILENAME
import ru.hh.plugins.code_modification.GradleConstants.BUILD_GRADLE_KTS_FILENAME
import ru.hh.plugins.code_modification.utils.searchGradlePsiFile
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.models.gradle.BuildGradleDependency

/**
 * Service for adding dependencies into build.gradle files.
 */
class BuildGradleModificationService(
    private val project: Project
) {

    companion object {
        private const val COMMAND_NAME = "BuildGradleModificationCommand"

        fun getInstance(project: Project) = BuildGradleModificationService(project)
    }

    fun addDepsIntoModule(
        module: Module,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            val buildGradlePsiFile = module.searchGradlePsiFile(BUILD_GRADLE_FILENAME)
                ?: throw IllegalStateException(
                    "Can't find `$BUILD_GRADLE_FILENAME` or `$BUILD_GRADLE_KTS_FILENAME` file in ${module.name}"
                )

            buildGradlePsiFile.addGradleDependencies(gradleDependencies)
        }
    }

    fun addDepsIntoFile(
        psiFile: PsiFile,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            psiFile.addGradleDependencies(gradleDependencies)
        }
    }

    fun addDepsInModuleDirectory(
        rootDir: PsiDirectory?,
        gradleDependencies: List<BuildGradleDependency>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            val buildGradlePsiFile = rootDir?.findFile(BUILD_GRADLE_FILENAME)
                ?: rootDir?.findFile(BUILD_GRADLE_KTS_FILENAME)

            if (buildGradlePsiFile == null) {
                HHLogger.e(
                    "Can't find `$BUILD_GRADLE_FILENAME` or `$BUILD_GRADLE_KTS_FILENAME` file in rootDir ($rootDir)"
                )
                @Suppress("detekt.LabeledExpression")
                return@wrapInCommand
            }

            buildGradlePsiFile.addGradleDependencies(gradleDependencies)
        }
    }

    fun addGradlePluginsInModuleDirectory(
        rootDir: PsiDirectory?,
        pluginsIds: List<String>,
        isInWriteCommand: Boolean = false
    ) {
        wrapInCommand(isInWriteCommand) {
            @Suppress("detekt.LabeledExpression")
            val buildGradleFile = rootDir?.findFile(BUILD_GRADLE_FILENAME)
                ?: rootDir?.findFile(BUILD_GRADLE_KTS_FILENAME)
                ?: return@wrapInCommand

            buildGradleFile.addGradlePlugins(pluginsIds)
        }
    }

    private fun PsiFile.addGradlePlugins(pluginsIds: List<String>) {
        when (this) {
            is KtFile -> {
                KtScriptsModificationService().addGradlePlugins(this, pluginsIds)
            }

            is GroovyFileImpl -> {
                GroovyScriptsModificationService().addGradlePlugins(this, pluginsIds)
            }

            else -> {
                throw IllegalArgumentException("Unknown $BUILD_GRADLE_FILENAME / $BUILD_GRADLE_KTS_FILENAME file type!")
            }
        }
    }

    private fun PsiFile.addGradleDependencies(gradleDependencies: List<BuildGradleDependency>) {
        when (this) {
            is KtFile -> {
                KtScriptsModificationService().addGradleDependencies(this, gradleDependencies)
            }

            is GroovyFileImpl -> {
                GroovyScriptsModificationService().addGradleDependencies(this, gradleDependencies)
            }

            else -> {
                throw IllegalArgumentException("Unknown $BUILD_GRADLE_FILENAME / $BUILD_GRADLE_KTS_FILENAME file type!")
            }
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
}
