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
import ru.hh.plugins.geminio.config.editor.GeminioPluginSettings
import java.io.File


/**
 * This code will be executed on project's startup.
 */
class GeminioStartupActivity : StartupActivity {

    companion object {
        private const val BASE_ID = "ru.hh.plugins.geminio.actions."
        private const val NEW_GROUP_ID_SUFFIX = "NewGroup."
        private const val GENERATE_GROUP_ID_SUFFIX = "GenerateGroup."
    }


    override fun runActivity(project: Project) {
        DumbService.getInstance(project).runWhenSmart {
            println("GeminioStartupActivity::")
            val pluginConfig = GeminioPluginSettings.getConfig(project)

            val pathToConfig = pluginConfig.configFilePath
            val pathToTemplates = project.basePath + pluginConfig.templatesRootDirPath

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
                val hhTemplatesNewGroup = actionManager.getAction(GeminioConstants.HH_TEMPLATES_NEW_GROUP_ID)
                        as DefaultActionGroup
                hhTemplatesNewGroup.templatePresentation.text = pluginConfig.groupsNames.forNewGroup
                val hhTemplatesGenerateGroup = actionManager.getAction(GeminioConstants.HH_TEMPLATES_GENERATE_GROUP_ID)
                        as DefaultActionGroup
                hhTemplatesGenerateGroup.templatePresentation.text = pluginConfig.groupsNames.forGenerateGroup

                templatesDirectories.forEach { templateName ->
                    val newActionForNewGroup = createActionForTemplate(
                        templatesRootDirPath = pathToTemplates,
                        templateDirName = templateName,
                        actionManager = actionManager,
                        actionId = BASE_ID + NEW_GROUP_ID_SUFFIX + templateName.toUnderlines()
                    )
                    hhTemplatesNewGroup += newActionForNewGroup

                    val newActionForGenerateGroup = createActionForTemplate(
                        templatesRootDirPath = pathToTemplates,
                        templateDirName = templateName,
                        actionManager = actionManager,
                        actionId = BASE_ID + GENERATE_GROUP_ID_SUFFIX + templateName.toUnderlines()
                    )
                    hhTemplatesGenerateGroup += newActionForGenerateGroup
                }
            }

            println("GeminioStartupActivity::END")
            println("==============================================")
        }
    }


    private fun createActionForTemplate(
        templatesRootDirPath: String,
        templateDirName: String,
        actionManager: ActionManager,
        actionId: String,
    ): AnAction {
        return ExecuteGeminioTemplateAction(
            actionText = templateDirName,
            actionDescription = "Action for executing '$templateDirName'",
            geminioRecipePath = getGeminioRecipeFilePath(templatesRootDirPath, templateDirName)
        ).also { action ->
            actionManager.registerAction(actionId, action)
        }
    }

    private fun getGeminioRecipeFilePath(templatesRootDirPath: String, templateDirName: String): String {
        return "$templatesRootDirPath/$templateDirName/${GeminioConstants.GEMINIO_TEMPLATE_CONFIG_FILE_NAME}"
    }

    private operator fun DefaultActionGroup.plusAssign(action: AnAction) {
        this.add(action)
    }


}