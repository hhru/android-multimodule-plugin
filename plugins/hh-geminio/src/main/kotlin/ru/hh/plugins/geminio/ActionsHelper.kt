package ru.hh.plugins.geminio

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import ru.hh.plugins.extensions.toUnderlines
import ru.hh.plugins.geminio.GeminioConstants.GEMINIO_TEMPLATE_CONFIG_FILE_NAME
import ru.hh.plugins.geminio.actions.RescanTemplatesAction
import ru.hh.plugins.geminio.actions.SetupGeminioConfigAction
import ru.hh.plugins.geminio.actions.module_template.ExecuteGeminioModuleTemplateAction
import ru.hh.plugins.geminio.actions.template.ExecuteGeminioTemplateAction
import ru.hh.plugins.geminio.config.GeminioPluginConfig
import ru.hh.plugins.geminio.config.editor.GeminioPluginSettings
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications
import java.io.File

internal class ActionsHelper {

    private companion object {
        const val BASE_ID = "ru.hh.plugins.geminio.actions."
        const val NEW_GROUP_ID_SUFFIX = "NewGroup."
        const val GENERATE_GROUP_ID_SUFFIX = "GenerateGroup."

        const val LOG_DIVIDER = "============"
    }


    fun createGeminioActions(project: Project) {
        val pluginConfig = GeminioPluginSettings.getConfig(project)
        val pathToConfig = pluginConfig.configFilePath
        val pathToTemplates = project.basePath + pluginConfig.templatesRootDirPath
        val pathToModulesTemplates = project.basePath + pluginConfig.modulesTemplatesRootDirPath

        HHLogger.d("\tpathToConfig: $pathToConfig")
        HHLogger.d("\tpathToTemplates: $pathToTemplates")
        HHLogger.d("\tpathToModulesTemplates: $pathToModulesTemplates")
        HHLogger.d(LOG_DIVIDER)
        HHLogger.d(LOG_DIVIDER)

        resetGeminioActions(project)

        if (project.isConfigNotValid(pathToConfig, pathToTemplates, pathToModulesTemplates)) {
            val error = "Geminio's config is not valid (may be not configured at all) -> no need to create actions"
            HHNotifications.error(message = error, action = SetupGeminioConfigAction())
            HHLogger.d("\t$error")
            return
        }

        createActionsForTemplates(
            project = project,
            pluginConfig = pluginConfig,
            rootDirPath = pathToTemplates,
            isModulesTemplates = false,
        )

        createActionsForTemplates(
            project = project,
            pluginConfig = pluginConfig,
            rootDirPath = pathToModulesTemplates,
            isModulesTemplates = true,
        )
    }

    fun resetGeminioActions(project: Project) {
        val actionManager = ActionManager.getInstance()
        val pluginConfig = GeminioPluginSettings.getConfig(project)
        actionManager.removeAllActionsFromGroups(pluginConfig, isModulesTemplates = true)
        actionManager.removeAllActionsFromGroups(pluginConfig, isModulesTemplates = false)
    }


    private fun createActionsForTemplates(
        project: Project,
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
            HHLogger.d("Templates directory doesn't exists [path: $rootDirPath, isModulesTemplates: $isModulesTemplates]")
            return
        }

        HHLogger.d("\tTemplates directory exists [path: $rootDirPath, isModulesTemplates: $isModulesTemplates]")
        val templatesDirs = rootDirectory.getSubfolderNamesWithRecipes()

        HHLogger.d("\tTemplates count: ${templatesDirs.size}")
        HHLogger.d(LOG_DIVIDER)

        actionManager.addTemplatesActions(
            projectName = project.name,
            templatesDirs = templatesDirs,
            rootDirPath = rootDirPath,
            isModulesTemplates = isModulesTemplates,
            groups = Groups(hhNewGroup = hhNewGroup, hhGenerateGroup = hhGenerateGroup),
        )

        actionManager.addRescanActions(
            projectName = project.name,
            groups = Groups(hhNewGroup = hhNewGroup, hhGenerateGroup = hhGenerateGroup),
        )
    }

    private fun ActionManager.removeAllActionsFromGroups(
        pluginConfig: GeminioPluginConfig,
        isModulesTemplates: Boolean,
    ) {
        val bundle = getTemplateActionsBundle(pluginConfig, isModulesTemplates)
        val hhNewGroup = getAction(bundle.templatesNewGroupId) as DefaultActionGroup
        val hhGenerateGroup = getAction(bundle.templatesGenerateGroupId) as DefaultActionGroup

        hhNewGroup.removeAll()
        hhGenerateGroup.removeAll()
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
        projectName: String,
        templatesDirs: List<String>,
        rootDirPath: String,
        isModulesTemplates: Boolean,
        groups: Groups,
    ) {
        templatesDirs.forEach { templateName ->
            createActionForTemplate(
                templatesRootDirPath = rootDirPath,
                templateDirName = templateName,
                isModulesTemplates = isModulesTemplates,
                actionManager = this,
                actionId = BASE_ID + NEW_GROUP_ID_SUFFIX + "$projectName." + templateName.toUnderlines(),
            ).also(groups.hhNewGroup::add)

            createActionForTemplate(
                templatesRootDirPath = rootDirPath,
                templateDirName = templateName,
                isModulesTemplates = isModulesTemplates,
                actionManager = this,
                actionId = BASE_ID + GENERATE_GROUP_ID_SUFFIX + "$projectName." + templateName.toUnderlines(),
            ).also(groups.hhGenerateGroup::add)
        }
    }

    private fun ActionManager.addRescanActions(
        projectName: String,
        groups: Groups,
    ) {
        val actionId = "$BASE_ID$projectName.RescanActionId"
        val rescanTemplatesAction = RescanTemplatesAction()

        getActionOrStub(actionId)
            ?.also { replaceAction(actionId, rescanTemplatesAction) }
            ?: registerAction(actionId, rescanTemplatesAction)

        groups.hhNewGroup.add(rescanTemplatesAction)
        groups.hhGenerateGroup.add(rescanTemplatesAction)
    }

    private fun File.getSubfolderNamesWithRecipes(): List<String> {
        return listFiles { file, _ -> file.isDirectory }
            ?.filter { file ->
                file.listFiles { _, name -> name == GEMINIO_TEMPLATE_CONFIG_FILE_NAME }.isNullOrEmpty().not()
            }
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

    private data class Groups(
        val hhNewGroup: DefaultActionGroup,
        val hhGenerateGroup: DefaultActionGroup,
    )

}
