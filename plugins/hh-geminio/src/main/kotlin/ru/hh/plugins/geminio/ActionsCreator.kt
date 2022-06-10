package ru.hh.plugins.geminio

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import ru.hh.plugins.extensions.toUnderlines
import ru.hh.plugins.geminio.actions.RescanTemplatesAction
import ru.hh.plugins.geminio.actions.SetupGeminioConfigAction
import ru.hh.plugins.geminio.actions.module_template.ExecuteGeminioModuleTemplateAction
import ru.hh.plugins.geminio.actions.template.ExecuteGeminioTemplateAction
import ru.hh.plugins.geminio.config.GeminioPluginConfig
import ru.hh.plugins.geminio.config.editor.GeminioPluginSettings
import ru.hh.plugins.geminio.services.balloonError
import java.io.File

internal class ActionsCreator {

    private companion object {
        const val BASE_ID = "ru.hh.plugins.geminio.actions."
        const val NEW_GROUP_ID_SUFFIX = "NewGroup."
        const val GENERATE_GROUP_ID_SUFFIX = "GenerateGroup."

        const val LOG_DIVIDER = "============"
    }


    fun create(project: Project) {
        val pluginConfig = GeminioPluginSettings.getConfig(project)
        val pathToConfig = pluginConfig.configFilePath
        val pathToTemplates = project.basePath + pluginConfig.templatesRootDirPath
        val pathToModulesTemplates = project.basePath + pluginConfig.modulesTemplatesRootDirPath

        println("\tpathToConfig: $pathToConfig")
        println("\tpathToTemplates: $pathToTemplates")
        println("\tpathToModulesTemplates: $pathToModulesTemplates")
        println(LOG_DIVIDER)
        println(LOG_DIVIDER)

        if (project.isConfigNotValid(pathToConfig, pathToTemplates, pathToModulesTemplates)) {
            val error = "Geminio's config is not valid (may be not configured at all) -> no need to create actions"
            project.balloonError(message = error, action = SetupGeminioConfigAction())
            println("\t$error")
            return
        }

        createActionsForTemplates(
            pluginConfig = pluginConfig,
            rootDirPath = pathToTemplates,
            isModulesTemplates = false,
        )

        createActionsForTemplates(
            pluginConfig = pluginConfig,
            rootDirPath = pathToModulesTemplates,
            isModulesTemplates = true,
        )
    }


    private fun createActionsForTemplates(
        pluginConfig: GeminioPluginConfig,
        rootDirPath: String,
        isModulesTemplates: Boolean,
    ) {
        val actionManager = ActionManager.getInstance()
        val bundle = getTemplateActionsBundle(pluginConfig, isModulesTemplates)
        val hhNewGroup = actionManager.getAction(bundle.templatesNewGroupId) as DefaultActionGroup
        hhNewGroup.templatePresentation.text = bundle.templatesNewGroupName

        val hhGenerateGroup = actionManager.getAction(bundle.templatesGenerateGroupId) as DefaultActionGroup
        val rootDirectory = File(rootDirPath)

        if (rootDirectory.exists().not() || rootDirectory.isDirectory.not()) {
            println("Templates directory doesn't exists [path: $rootDirPath, isModulesTemplates: $isModulesTemplates]")
            return
        }

        println("\tTemplates directory exists [path: $rootDirPath, isModulesTemplates: $isModulesTemplates]")
        val templatesDirs = rootDirectory.getSubfolderNamesWithRecipes()

        println("\tTemplates count: ${templatesDirs.size}")
        println(LOG_DIVIDER)

        hhNewGroup.removeAll()
        hhGenerateGroup.removeAll()

        actionManager.addTemplatesActions(
            templatesDirs = templatesDirs,
            rootDirPath = rootDirPath,
            isModulesTemplates = isModulesTemplates,
            hhNewGroup = hhNewGroup,
            hhGenerateGroup = hhGenerateGroup,
        )

        actionManager.addRescanActions(hhNewGroup, hhGenerateGroup)
    }

    private fun createActionForTemplate(
        templatesRootDirPath: String,
        templateDirName: String,
        isModulesTemplates: Boolean,
        actionManager: ActionManager,
        actionId: String,
    ): AnAction {
        val action = when (isModulesTemplates) {
            true -> {
                ExecuteGeminioModuleTemplateAction(
                    actionText = templateDirName,
                    actionDescription = "Action for executing '$templateDirName'",
                    geminioRecipePath = getGeminioRecipeFilePath(templatesRootDirPath, templateDirName),
                )
            }

            false -> {
                ExecuteGeminioTemplateAction(
                    actionText = templateDirName,
                    actionDescription = "Action for executing '$templateDirName'",
                    geminioRecipePath = getGeminioRecipeFilePath(templatesRootDirPath, templateDirName),
                )
            }
        }

        with(actionManager) {
            getActionOrStub(actionId)
                ?.also { replaceAction(actionId, action) }
                ?: registerAction(actionId, action)
        }

        return action
    }

    private fun getGeminioRecipeFilePath(templatesRootDirPath: String, templateDirName: String): String {
        return "$templatesRootDirPath/$templateDirName/${GeminioConstants.GEMINIO_TEMPLATE_CONFIG_FILE_NAME}"
    }

    private fun getTemplateActionsBundle(
        pluginConfig: GeminioPluginConfig,
        isModulesTemplates: Boolean,
    ): TemplateActionsBundle = when (isModulesTemplates) {
        true -> {
            TemplateActionsBundle(
                templatesNewGroupId = GeminioConstants.HH_MODULES_TEMPLATES_NEW_GROUP_ID,
                templatesGenerateGroupId = GeminioConstants.HH_MODULES_TEMPLATES_GENERATE_GROUP_ID,
                templatesNewGroupName = pluginConfig.groupsNames.forNewModulesGroup,
            )
        }

        false -> {
            TemplateActionsBundle(
                templatesNewGroupId = GeminioConstants.HH_TEMPLATES_NEW_GROUP_ID,
                templatesGenerateGroupId = GeminioConstants.HH_TEMPLATES_GENERATE_GROUP_ID,
                templatesNewGroupName = pluginConfig.groupsNames.forNewGroup,
            )
        }
    }

    private fun ActionManager.addTemplatesActions(
        templatesDirs: List<String>,
        rootDirPath: String,
        isModulesTemplates: Boolean,
        hhNewGroup: DefaultActionGroup,
        hhGenerateGroup: DefaultActionGroup
    ) {
        templatesDirs.forEach { templateName ->
            createActionForTemplate(
                templatesRootDirPath = rootDirPath,
                templateDirName = templateName,
                isModulesTemplates = isModulesTemplates,
                actionManager = this,
                actionId = BASE_ID + NEW_GROUP_ID_SUFFIX + templateName.toUnderlines(),
            ).also(hhNewGroup::add)

            createActionForTemplate(
                templatesRootDirPath = rootDirPath,
                templateDirName = templateName,
                isModulesTemplates = isModulesTemplates,
                actionManager = this,
                actionId = BASE_ID + GENERATE_GROUP_ID_SUFFIX + templateName.toUnderlines(),
            ).also(hhGenerateGroup::add)
        }
    }

    private fun ActionManager.addRescanActions(
        hhNewGroup: DefaultActionGroup,
        hhGenerateGroup: DefaultActionGroup,
    ) {
        val actionId = BASE_ID + "RescanActionId"
        val rescanTemplatesAction = RescanTemplatesAction()

        getActionOrStub(actionId)
            ?.also { replaceAction(actionId, rescanTemplatesAction) }
            ?: registerAction(actionId, rescanTemplatesAction)

        hhNewGroup.add(rescanTemplatesAction)
        hhGenerateGroup.add(rescanTemplatesAction)
    }

    private fun File.getSubfolderNamesWithRecipes(): List<String> {
        return listFiles { file, _ -> file.isDirectory }
            ?.filter { file -> file.listFiles { _, name -> name == "recipe.yaml" }.isNullOrEmpty().not() }
            ?.map { it.name }
            ?: emptyList()
    }

    private fun Project.isConfigNotValid(
        pathToConfig: String,
        pathToTemplates: String,
        pathToModulesTemplates: String,
    ): Boolean {
        return pathToConfig.isBlank() || pathToTemplates == basePath || pathToModulesTemplates == basePath
    }


    private data class TemplateActionsBundle(
        val templatesNewGroupId: String,
        val templatesGenerateGroupId: String,
        val templatesNewGroupName: String,
    )

}
