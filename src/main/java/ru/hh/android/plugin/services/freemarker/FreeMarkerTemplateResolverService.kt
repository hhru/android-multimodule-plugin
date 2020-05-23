package ru.hh.android.plugin.services.freemarker

import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.model.RenderTemplateModel
import com.android.tools.idea.npw.template.ConfigureTemplateParametersStep
import com.android.tools.idea.npw.template.TemplateHandle
import com.android.tools.idea.projectsystem.NamedModuleTemplate
import com.android.tools.idea.templates.TemplateManager
import com.android.tools.idea.templates.recipe.RenderingContext
import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder
import com.android.tools.idea.util.androidFacet
import com.android.tools.idea.util.toIoFile
import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.util.module
import ru.hh.android.plugin.PluginConstants
import ru.hh.android.plugin.config.PluginConfig
import ru.hh.android.plugin.exceptions.WrongAndroidModuleConfigurationException
import ru.hh.android.plugin.extensions.getSelectedPsiElement
import ru.hh.android.plugin.extensions.packageName
import ru.hh.android.plugin.services.NotificationsFactory
import ru.hh.android.plugin.services.freemarker.utils.AndroidModulePathsData
import ru.hh.android.plugin.services.freemarker.utils.DryRunValuesInjector
import java.io.File


/**
 * Component for resolving FreeMarker templates
 */
@Service
class FreeMarkerTemplateResolverService(
    private val project: Project
) {

    companion object {
        private const val NAMED_MODULE_TEMPLATE_NAME = "FreeMarkerTemplateResolver"

        private const val COMMAND_NAME = "ru.hh.android.plugin.services.freemarker.FreeMarkerTemplateResolverService"


        fun newInstance(project: Project): FreeMarkerTemplateResolverService = project.service()
    }

    /**
     * Method to resolve template.xml inside some directory. Builds UI and handle files creation.
     *
     * @param event - Event with correct [org.jetbrains.android.facet.AndroidFacet].
     * @param templateDirName - Name of folder with resolved template (e.g. 'create_component')
     * @param dialogTitle - Main title in dialog (e.g. "Create new component")
     * @param afterFilesCreationAction - additional actions after files creation
     */
    fun resolveTemplate(
        event: AnActionEvent,
        templateDirName: String,
        dialogTitle: String,
        afterFilesCreationAction: (List<File>, Map<String, Any>) -> Unit = { _, _ -> }
    ) {
        event.project?.let { project ->
            val pluginConfig = PluginConfig.getInstance(project)

            if (pluginConfig.isDebugModeEnabled) {
                doDryRun(event, templateDirName)
            } else {
                resolveTemplateInternal(event, templateDirName, dialogTitle, afterFilesCreationAction)
            }
        }
    }

    private fun doDryRun(event: AnActionEvent, templateDirName: String) {
        val (project, androidFacet, currentModuleRootDir, currentModulePackageName) = fetchInitData(event)

        val templateParametersMap = mutableMapOf<String, Any>().also { paramsMap ->
            DryRunValuesInjector(project, androidFacet, currentModuleRootDir, currentModulePackageName)
                .injectValues(paramsMap)
        }

        val templateHandle = getTemplateHandle(project, templateDirName)

        val context = RenderingContext.Builder.newContext(templateHandle.template, project)
            .withCommandName(COMMAND_NAME)
            .withModule(androidFacet.module)
            .withParams(templateParametersMap)
            .build()

        val notificationsFactory = NotificationsFactory.getInstance(project)
        if (templateHandle.template.render(context, true)) {
            notificationsFactory.info("Template at `$templateDirName` has no errors")
        } else {
            notificationsFactory.error("Template at `$templateDirName` has some errors. Check stacktrace")
        }
    }

    private fun resolveTemplateInternal(
        event: AnActionEvent,
        templateDirName: String,
        dialogTitle: String,
        afterFilesCreationAction: (List<File>, Map<String, Any>) -> Unit
    ) {
        val (project, androidFacet, currentModuleRootDir, currentModulePackageName) = fetchInitData(event)

        val templateHandle = getTemplateHandle(project, templateDirName)
        val namedModuleTemplate = NamedModuleTemplate(
            name = NAMED_MODULE_TEMPLATE_NAME,
            paths = AndroidModulePathsData(currentModuleRootDir, currentModulePackageName)
        )
        val renderTemplateModel = RenderTemplateModel.fromFacet(
            facet = androidFacet,
            templateHandle = templateHandle,
            initialPackageSuggestion = currentModulePackageName,
            template = namedModuleTemplate,
            commandName = COMMAND_NAME,
            projectSyncInvoker = ProjectSyncInvoker.DefaultProjectSyncInvoker(),
            shouldOpenFiles = true
        )


        val templateWizardStep = ConfigureTemplateParametersStep(
            renderTemplateModel,
            dialogTitle,
            listOf(namedModuleTemplate)
        )

        val studioWizardModel = ModelWizard.Builder().addStep(templateWizardStep).build().apply {
            addResultListener(object : ModelWizard.WizardListener {
                override fun onWizardFinished(result: ModelWizard.WizardResult) {
                    super.onWizardFinished(result)
                    if (result.isFinished) {
                        afterFilesCreationAction.invoke(renderTemplateModel.createdFiles, renderTemplateModel.templateValues)
                    }
                }
            })
        }

        StudioWizardDialogBuilder(studioWizardModel, NAMED_MODULE_TEMPLATE_NAME)
            .setProject(project)
            .build()
            .show()
    }

    private fun fetchInitData(event: AnActionEvent): FreeMarkerResolverInitData {
        val project = event.project
        val selectedPsiElement = event.getSelectedPsiElement()

        val currentModule = selectedPsiElement?.module
        val currentModuleRootDirFile = currentModule?.moduleFile?.parent?.toIoFile()

        val currentModuleAndroidFacet = currentModule?.androidFacet
        val currentModulePackageName = currentModuleAndroidFacet?.packageName

        if (project == null || currentModuleRootDirFile == null || currentModulePackageName == null) {
            throw WrongAndroidModuleConfigurationException(
                """
                No information about module root directory or package name or even project. 
                Check ${currentModule?.name} module and it's AndroidManifest.xml
            """
            )
        }

        return FreeMarkerResolverInitData(
            project = project,
            androidFacet = currentModuleAndroidFacet,
            moduleRootDir = currentModuleRootDirFile,
            modulePackageName = currentModulePackageName
        )
    }

    private fun getTemplateHandle(project: Project, templateDirName: String): TemplateHandle {
        val pluginConfigDirPath = PluginConfig.getInstance(project).pluginFolderDirPath
        val templateDirPath = "$pluginConfigDirPath/${PluginConstants.DEFAULT_TEMPLATES_DIR_NAME}/$templateDirName"

        forceReloadAllTemplates(project)

        return TemplateHandle(File(templateDirPath))
    }

    private fun forceReloadAllTemplates(project: Project) {
        TemplateManager.getInstance().refreshDynamicTemplateMenu(project)
    }

}