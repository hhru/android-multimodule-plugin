package ru.hh.plugins.code_modification

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.plugins.code_modification.extensions.psi.getBreakLineElement
import ru.hh.plugins.code_modification.extensions.psi.getIncludeModuleExpression
import ru.hh.plugins.code_modification.extensions.psi.getIncludeModuleExpressionElement
import ru.hh.plugins.code_modification.extensions.psi.getIncludeModuleRelativePathSetupElement
import ru.hh.plugins.extensions.openapi.findPsiFileByName
import ru.hh.plugins.extensions.openapi.getRootModule


class SettingsGradleModificationService(
    private val project: Project
) {

    companion object {
        private const val COMMAND_NAME = "SettingsGradleModificationCommand"
        private const val SETTINGS_GRADLE_FILENAME = "settings.gradle"
        private const val SETTINGS_GRADLE_KTS_FILENAME = "settings.gradle.kts"

        fun getInstance(project: Project) = SettingsGradleModificationService(project)
    }


    /**
     * Adds module description into settings.gradle / settings.gradle.kts file.
     * <code>
     * include(":moduleName")
     * project(":moduleName").projectDir = new File(settingsDir, "shared/core/moduleName")
     * </code>
     *
     * @param moduleName - module name without ':', e.g. "mylibrary"
     * @param moduleRelativePath - relative path for module directory from settings.gradle, e.g. 'shared/core/mylibrary'
     */
    fun addGradleModuleDescription(moduleName: String, moduleRelativePath: String) {
        val rootModule = project.getRootModule()
        val settingsGradlePsiFile = rootModule.findPsiFileByName(SETTINGS_GRADLE_FILENAME)
            ?: rootModule.findPsiFileByName(SETTINGS_GRADLE_KTS_FILENAME)
            ?: throw IllegalStateException("Can't find $SETTINGS_GRADLE_FILENAME / $SETTINGS_GRADLE_KTS_FILENAME file!")

        handleSettingsGradleFile(settingsGradlePsiFile, moduleName, moduleRelativePath)
    }

    private fun handleSettingsGradleFile(
        settingsGradlePsiFile: PsiFile,
        moduleName: String,
        moduleRelativePath: String
    ) {
        when (settingsGradlePsiFile) {
            is KtFile -> {
                settingsGradlePsiFile.addModuleDescription(moduleName, moduleRelativePath)
            }

            is GroovyFile -> {
                settingsGradlePsiFile.addModuleDescription(moduleName, moduleRelativePath)
            }

            else -> {
                throw IllegalArgumentException(
                    """
                Unknown file type for adding module description!
                    file name: ${settingsGradlePsiFile.name}    
                    file path: ${settingsGradlePsiFile.virtualFile.canonicalPath}
                    module name: $moduleName
                    module relative path: $moduleRelativePath
                """
                )
            }
        }
    }

    private fun KtFile.addModuleDescription(
        moduleName: String,
        moduleRelativePath: String
    ) {
        val factory = KtPsiFactory(project)

        project.executeWriteCommand(COMMAND_NAME) {
            with(this) {
                add(factory.createNewLine())
                add(factory.getIncludeModuleExpression(moduleName))
                add(factory.createNewLine())
                add(factory.getIncludeModuleRelativePathSetupElement(moduleName, moduleRelativePath))
                add(factory.createNewLine())
            }
        }
    }

    private fun GroovyFile.addModuleDescription(
        moduleName: String,
        moduleRelativePath: String
    ) {
        val factory = GroovyPsiElementFactory.getInstance(project)

        project.executeWriteCommand(COMMAND_NAME) {
            with(this) {
                add(factory.getBreakLineElement())
                add(factory.getIncludeModuleExpressionElement(moduleName))
                add(factory.getBreakLineElement())
                add(factory.getIncludeModuleRelativePathSetupElement(moduleName, moduleRelativePath))
                add(factory.getBreakLineElement())
            }
        }
    }

}