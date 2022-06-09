package ru.hh.plugins.geminio.services

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import ru.hh.plugins.extensions.toUnderlines
import ru.hh.plugins.geminio.GeminioConstants
import ru.hh.plugins.geminio.actions.module_template.ExecuteGeminioModuleTemplateAction
import ru.hh.plugins.geminio.actions.template.ExecuteGeminioTemplateAction
import ru.hh.plugins.geminio.config.GeminioPluginConfig
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
            val pathToModulesTemplates = project.basePath + pluginConfig.modulesTemplatesRootDirPath

            println("\tpathToConfig: $pathToConfig")
            println("\tpathToTemplates: $pathToTemplates")
            println("\tpathToModulesTemplates: $pathToModulesTemplates")
            println("============")
            println("============")

            if (project.isConfigNotValid(pathToConfig, pathToTemplates, pathToModulesTemplates)) {
                println("\tGeminio's config is not valid (may be not configured at all) -> no need to create actions")
                return@runWhenSmart
            }

            createActionsForTemplates(
                pluginConfig = pluginConfig,
                rootDirPath = pathToTemplates,
                isModulesTemplates = false
            )
            createActionsForTemplates(pluginConfig, pathToModulesTemplates, true)

            println("GeminioStartupActivity::END")
            println("==============================================")
        }
    }

    private fun createActionsForTemplates(
        pluginConfig: GeminioPluginConfig,
        rootDirPath: String,
        isModulesTemplates: Boolean
    ) {
        val rootDirectory = File(rootDirPath)
        if (rootDirectory.exists().not() || rootDirectory.isDirectory.not()) {
            println("Templates directory doesn't exists [path: $rootDirPath, isModulesTemplates: $isModulesTemplates]")
            return
        }

        println("\tTemplates directory exists [path: $rootDirPath, isModulesTemplates: $isModulesTemplates]")
        val templatesDirs = rootDirectory.listFiles { file, _ -> file.isDirectory }
            ?.filter { file -> file.listFiles { _, name -> name == "recipe.yaml" }.isNullOrEmpty().not() }
            ?.map { it.name }
            ?: emptyList()

        println("\tTemplates count: ${templatesDirs.size}")
        println("============")

        val actionManager = ActionManager.getInstance()

        val bundle = getTemplateActionsBundle(pluginConfig, isModulesTemplates)

        val hhNewGroup = actionManager.getAction(bundle.templatesNewGroupId) as DefaultActionGroup
        hhNewGroup.templatePresentation.text = bundle.templatesNewGroupName
        val hhGenerateGroup = actionManager.getAction(bundle.templatesGenerateGroupId) as DefaultActionGroup

        templatesDirs.forEach { templateName ->
            val newActionForNewGroup = createActionForTemplate(
                templatesRootDirPath = rootDirPath,
                templateDirName = templateName,
                isModulesTemplates = isModulesTemplates,
                actionManager = actionManager,
                actionId = BASE_ID + NEW_GROUP_ID_SUFFIX + templateName.toUnderlines()
            )
            hhNewGroup += newActionForNewGroup

            val newActionForGenerateGroup = createActionForTemplate(
                templatesRootDirPath = rootDirPath,
                templateDirName = templateName,
                isModulesTemplates = isModulesTemplates,
                actionManager = actionManager,
                actionId = BASE_ID + GENERATE_GROUP_ID_SUFFIX + templateName.toUnderlines()
            )
            hhGenerateGroup += newActionForGenerateGroup
        }
    }

    private fun createActionForTemplate(
        templatesRootDirPath: String,
        templateDirName: String,
        isModulesTemplates: Boolean,
        actionManager: ActionManager,
        actionId: String,
    ): AnAction {
        val action = when {
            isModulesTemplates -> {
                ExecuteGeminioModuleTemplateAction(
                    actionText = templateDirName,
                    actionDescription = "Action for executing '$templateDirName'",
                    geminioRecipePath = getGeminioRecipeFilePath(templatesRootDirPath, templateDirName)
                )
            }

            else -> {
                ExecuteGeminioTemplateAction(
                    actionText = templateDirName,
                    actionDescription = "Action for executing '$templateDirName'",
                    geminioRecipePath = getGeminioRecipeFilePath(templatesRootDirPath, templateDirName)
                )
            }
        }
        actionManager.registerAction(actionId, action)

        return action
    }

    private fun getGeminioRecipeFilePath(templatesRootDirPath: String, templateDirName: String): String {
        return "$templatesRootDirPath/$templateDirName/${GeminioConstants.GEMINIO_TEMPLATE_CONFIG_FILE_NAME}"
    }

    private fun getTemplateActionsBundle(
        pluginConfig: GeminioPluginConfig,
        isModulesTemplates: Boolean
    ): TemplateActionsBundle {
        return when {
            isModulesTemplates -> {
                TemplateActionsBundle(
                    GeminioConstants.HH_MODULES_TEMPLATES_NEW_GROUP_ID,
                    GeminioConstants.HH_MODULES_TEMPLATES_GENERATE_GROUP_ID,
                    pluginConfig.groupsNames.forNewModulesGroup
                )
            }

            else -> {
                TemplateActionsBundle(
                    GeminioConstants.HH_TEMPLATES_NEW_GROUP_ID,
                    GeminioConstants.HH_TEMPLATES_GENERATE_GROUP_ID,
                    pluginConfig.groupsNames.forNewGroup
                )
            }
        }
    }

    private data class TemplateActionsBundle(
        val templatesNewGroupId: String,
        val templatesGenerateGroupId: String,
        val templatesNewGroupName: String
    )

    private operator fun DefaultActionGroup.plusAssign(action: AnAction) {
        this.add(action)
    }

    private fun Project.isConfigNotValid(
        pathToConfig: String,
        pathToTemplates: String,
        pathToModulesTemplates: String
    ): Boolean {
        return pathToConfig.isBlank() || pathToTemplates == basePath || pathToModulesTemplates == basePath
    }
}
