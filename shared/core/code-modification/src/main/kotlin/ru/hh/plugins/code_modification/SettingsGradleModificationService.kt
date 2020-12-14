package ru.hh.plugins.code_modification

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.plugins.code_modification.extensions.psi.getBreakLineElement
import ru.hh.plugins.code_modification.extensions.psi.getIncludeModuleExpressionElement
import ru.hh.plugins.code_modification.extensions.psi.getIncludeModuleRelativePathSetupElement
import ru.hh.plugins.extensions.openapi.findPsiFileByName
import ru.hh.plugins.extensions.openapi.getRootModule


@Service
class SettingsGradleModificationService(
    private val project: Project
) {

    companion object {
        private const val COMMAND_NAME = "SettingsGradleModificationCommand"
        private const val SETTINGS_GRADLE_FILENAME = "settings.gradle"

        fun getInstance(project: Project): SettingsGradleModificationService = project.service()
    }


    /**
     * Adds module description into settings.gradle file.
     * <code>
     * include(":moduleName")
     * project(":moduleName").projectDir = new File(settingsDir, "shared/core/moduleName")
     * </code>
     *
     * @param moduleName - module name without ':', e.g. "mylibrary"
     * @param moduleRelativePath - relative path for module directory from settings.gradle, e.g. 'shared/core/mylibrary'
     */
    fun addGradleModuleDescription(moduleName: String, moduleRelativePath: String) {
        val settingsGradlePsiFile = project.getRootModule().findPsiFileByName(SETTINGS_GRADLE_FILENAME)
            ?: throw IllegalStateException("Can't find settings.gradle file!")

        val factory = GroovyPsiElementFactory.getInstance(project)

        project.executeWriteCommand(COMMAND_NAME) {
            settingsGradlePsiFile.add(factory.getBreakLineElement())

            listOf(
                factory.getIncludeModuleExpressionElement(moduleName),
                factory.getIncludeModuleRelativePathSetupElement(moduleName, moduleRelativePath)
            ).forEach { psiElement ->
                settingsGradlePsiFile.add(psiElement)
                settingsGradlePsiFile.add(factory.getBreakLineElement())
            }
        }
    }

}