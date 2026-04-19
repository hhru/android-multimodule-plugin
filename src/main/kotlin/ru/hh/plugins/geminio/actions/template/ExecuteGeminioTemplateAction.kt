package ru.hh.plugins.geminio.actions.template

import com.android.tools.idea.model.AndroidModel
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile
import ru.hh.plugins.dialog.sync.showSyncQuestionDialog
import ru.hh.plugins.extensions.getTargetDirectory
import ru.hh.plugins.extensions.packageName
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.sdk.form.GeminioFormSession
import ru.hh.plugins.geminio.sdk.form.applyFormValues
import ru.hh.plugins.geminio.sdk.form.toGeminioForm
import ru.hh.plugins.geminio.sdk.models.GeminioTemplateData
import ru.hh.plugins.geminio.services.templates.GeminioRecipeExecutorFactoryService
import ru.hh.plugins.geminio.services.templates.createGeminioNamedModuleTemplateContext
import ru.hh.plugins.geminio.wizard.GeminioFormDialog
import ru.hh.plugins.geminio.wizard.GeminioLoadingDialog
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications
import ru.hh.plugins.psi_utils.kotlin.shortReferencesAndReformatWithCodeStyle
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * Base action for executing templates from YAML config.
 *
 * This action not registered in plugin.xml, because we create it in runtime.
 */
class ExecuteGeminioTemplateAction(
    actionDescription: String,
    private val actionText: String,
    private val geminioRecipePath: String
) : AnAction(
    /* text = */
    actionText,
    /* description = */
    actionDescription,
    /* icon = */
    null
) {

    private companion object {
        const val COMMAND_NAME = "ExecuteGeminioTemplateAction.Command"
        const val DIALOG_TITLE = "Geminio template"
        const val LOADING_TITLE = "Generating Geminio template"
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val dataContext = e.dataContext

        val module = LangDataKeys.MODULE.getData(dataContext)
        val facet = module?.let { AndroidFacet.getInstance(it) }

        e.presentation
            .isEnabledAndVisible = (e.project == null || facet == null || AndroidModel.get(facet) == null).not()
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        HHLogger.d("Start executing template [$actionText]")

        val geminioSdk = GeminioSdkFactory.createGeminioSdk()
        val geminioRecipe = geminioSdk.parseYamlRecipe(geminioRecipePath)

        val (project, facet) = actionEvent.fetchEventData()

        val targetDirectory = actionEvent.getTargetDirectory()
        val geminioTemplateData = geminioSdk.createGeminioTemplateData(
            project = project,
            geminioRecipe = geminioRecipe,
            targetDirectory = targetDirectory,
        )
        val form = geminioRecipe.toGeminioForm()
        val formSession = GeminioFormSession(form)

        val dialog = GeminioFormDialog(
            project = project,
            title = "$DIALOG_TITLE: $actionText",
            templateName = geminioRecipe.requiredParams.name,
            templateDescription = geminioRecipe.requiredParams.description,
            form = form,
            session = formSession,
        )
        if (dialog.showAndGet().not()) {
            return
        }

        val templateContext = facet.createGeminioNamedModuleTemplateContext(targetDirectory)
        val recipeExecutorFactoryService = GeminioRecipeExecutorFactoryService(project)
        val targetPackageName = if (form.fieldsById.containsKey(FEATURE_PACKAGE_NAME_PARAMETER_ID)) {
            formSession.stringValue(FEATURE_PACKAGE_NAME_PARAMETER_ID).orEmpty()
        } else {
            templateContext.initialPackageSuggestion.ifBlank { facet.packageName }
        }

        val preparedExecution = recipeExecutorFactoryService.createRecipeExecutorForExistingModule(
            facet = facet,
            targetDirectory = targetDirectory,
            targetPackageName = targetPackageName,
        )

        geminioTemplateData.applyFormValues(form, formSession)

        executeTemplateWithLoader(
            project = project,
            actionEvent = actionEvent,
            geminioTemplateData = geminioTemplateData,
            preparedExecution = preparedExecution,
        )
    }

    private fun applyShortenReferencesAndCodeStyle(
        project: Project,
        createdFiles: Collection<File>,
    ) {
        measureTimeMillis {
            createdFiles.forEach { file ->
                val psiFile = file.toPsiFile(project) as? KtFile
                psiFile?.shortReferencesAndReformatWithCodeStyle()
            }
        }.also { HHLogger.d("Shorten references time: $it ms") }
    }

    private fun executeTemplateWithLoader(
        project: Project,
        actionEvent: AnActionEvent,
        geminioTemplateData: GeminioTemplateData,
        preparedExecution: GeminioRecipeExecutorFactoryService.PreparedExistingModuleRecipeExecution,
    ) {
        val loadingDialog = GeminioLoadingDialog(
            project = project,
            title = LOADING_TITLE,
            description = "Generating '$actionText' template. Please wait...",
        )
        loadingDialog.isVisible = true

        ApplicationManager.getApplication().invokeLater {
            var completedSuccessfully = false
            try {
                project.executeWriteCommand(COMMAND_NAME) {
                    geminioTemplateData.androidStudioTemplate.recipe.invoke(
                        preparedExecution.recipeExecutor,
                        preparedExecution.moduleTemplateData,
                    )
                    applyShortenReferencesAndCodeStyle(
                        project = project,
                        createdFiles = preparedExecution.createdFiles,
                    )
                }
                completedSuccessfully = true
            } catch (error: Throwable) {
                HHLogger.e("Template '$actionText' execution failed:\n${error.stackTraceToString()}")
                HHNotifications.error(
                    message = "Some error occurred when '$actionText' executed. " +
                            "Check warnings at the bottom right corner."
                )
            } finally {
                loadingDialog.dispose()
            }

            if (completedSuccessfully) {
                project.showSyncQuestionDialog(syncPerformedActionEvent = actionEvent)
                HHNotifications.info(message = "Finished '$actionText' template execution")
            }
        }
    }

    private fun AnActionEvent.fetchEventData(): EventData {
        val dataContext = dataContext

        val module = LangDataKeys.MODULE.getData(dataContext)
        val facet = module?.let { AndroidFacet.getInstance(it) }

        return EventData(
            project = requireNotNull(project),
            androidFacet = requireNotNull(facet)
        )
    }

    private data class EventData(
        val project: Project,
        val androidFacet: AndroidFacet
    )
}
