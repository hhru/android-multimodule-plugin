package ru.hh.plugins.geminio.services

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import ru.hh.plugins.extensions.toUnderlines
import ru.hh.plugins.geminio.GeminioConstants
import ru.hh.plugins.geminio.actions.ExecuteGeminioTemplateAction
import java.io.File


/**
 * Код отрабатывает при старте проекта
 */
class GeminioStartupActivity : StartupActivity {

    companion object {
        private const val GEMINIO_CONFIG_DIR_PATH = "/android-style-guide/geminio"
        private const val GEMINIO_TEMPLATES_DIR_NAME = "/templates"
    }


    override fun runActivity(project: Project) {
        DumbService.getInstance(project).runWhenSmart {
            println("GeminioStartupActivity::")
            val pathToConfig = project.basePath + GEMINIO_CONFIG_DIR_PATH
            val pathToTemplates = pathToConfig + GEMINIO_TEMPLATES_DIR_NAME

            println("\tpathToConfig: $pathToConfig")
            println("\tpathToTemplates: $pathToTemplates")

            val templatesRootDirectory = File(pathToTemplates)
            if (templatesRootDirectory.exists() && templatesRootDirectory.isDirectory) {
                println("\tTemplates directory exists")
                val templatesDirectories = templatesRootDirectory.list { file, _ ->
                    file.isDirectory
                } ?: emptyArray()

                println("\tTemplates count: ${templatesDirectories.size}")
                println("============")

                val actionManager = ActionManager.getInstance()
                val hhTemplatesGroup = actionManager.getAction(GeminioConstants.HH_TEMPLATES_GROUP_Id)
                        as DefaultActionGroup

                templatesDirectories.forEach { templateName ->
                    val newAction = createActionForTemplate(pathToTemplates, templateName)
                    val newActionId = ExecuteGeminioTemplateAction.BASE_ID + templateName.toUnderlines()

                    actionManager.registerAction(newActionId, newAction)

                    hhTemplatesGroup.add(newAction)
                }
            }

            println("GeminioStartupActivity::END")
            println("==============================================")
        }
    }


    private fun createActionForTemplate(
        templatesRootDirPath: String,
        templateDirName: String
    ): AnAction {
        return ExecuteGeminioTemplateAction(
            actionText = templateDirName,
            actionDescription = "Action for executing '$templateDirName'",
            templateDirPath = getGeminioTemplateDirPath(templatesRootDirPath, templateDirName),
            geminioRecipePath = getGeminioRecipeFilePath(templatesRootDirPath, templateDirName)
        )
    }

    private fun getGeminioTemplateDirPath(templatesRootDirPath: String, templateDirName: String): String {
        return "$templatesRootDirPath/$templateDirName"
    }

    private fun getGeminioRecipeFilePath(templatesRootDirPath: String, templateDirName: String): String {
        return "$templatesRootDirPath/$templateDirName/${GeminioConstants.GEMINIO_TEMPLATE_CONFIG_FILE_NAME}"
    }


}