package ru.hh.android.plugin.services.code_generator

import com.intellij.openapi.command.executeCommand
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.android.plugin.exceptions.NoFileFoundException
import ru.hh.android.plugin.extensions.findPsiFileByName
import ru.hh.android.plugin.extensions.getRootModule
import ru.hh.android.plugin.extensions.psi.groovy.getBreakLineElement
import ru.hh.android.plugin.extensions.psi.groovy.getIncludeModuleExpressionElement
import ru.hh.android.plugin.extensions.psi.groovy.getIncludeModuleRelativePathSetupElement

@Service
class SettingsGradleModificationService {

    companion object {
        private const val SETTINGS_GRADLE_FILENAME = "settings.gradle"

        fun newInstance(project: Project): SettingsGradleModificationService = project.service()
    }


    fun addGradleModuleDescription(
        project: Project,
        moduleName: String,
        moduleRelativePath: String
    ) {
        val settingsGradlePsiFile = project.getRootModule().findPsiFileByName(SETTINGS_GRADLE_FILENAME)
            ?: throw NoFileFoundException("Can't find settings.gradle file!")
        val factory = GroovyPsiElementFactory.getInstance(project)

        executeCommand {
            runWriteAction {
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

}