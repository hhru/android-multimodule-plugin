package ru.hh.plugins.geminio.actions.template

import com.android.tools.idea.model.AndroidModel
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.dialog.sync.showSyncQuestionDialog
import ru.hh.plugins.extensions.getTargetDirectory
import ru.hh.plugins.extensions.packageName
import ru.hh.plugins.freemarker_wrapper.FreemarkerConfiguration
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.sdk.execution.GeminioGeneratedFilesPostProcessor
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeExecutionRequest
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipePathContextFactory
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeRunner
import ru.hh.plugins.geminio.sdk.execution.GeminioTemplateParametersFactory
import ru.hh.plugins.geminio.sdk.form.GeminioFormSession
import ru.hh.plugins.geminio.sdk.form.toGeminioForm
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.services.android.createGeminioNamedModuleTemplateContext
import ru.hh.plugins.geminio.wizard.GeminioFormDialog
import ru.hh.plugins.geminio.wizard.GeminioLoadingDialog
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications
import java.io.IOException

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
        val targetPackageName = if (form.fieldsById.containsKey(FEATURE_PACKAGE_NAME_PARAMETER_ID)) {
            formSession.stringValue(FEATURE_PACKAGE_NAME_PARAMETER_ID).orEmpty()
        } else {
            templateContext.initialPackageSuggestion.ifBlank { facet.packageName }
        }
        val pathContext = GeminioRecipePathContextFactory.createForExistingModule(
            facet = facet,
            targetDirectory = targetDirectory,
            targetPackageName = targetPackageName,
        )
        val executionRequest = GeminioRecipeExecutionRequest(
            project = project,
            pathContext = pathContext,
            templateParameters = GeminioTemplateParametersFactory.create(
                session = formSession,
                packageName = targetPackageName,
                applicationPackageName = facet.packageName.ifBlank { targetPackageName },
                currentDirPath = requireNotNull(pathContext.currentDirOut),
            ),
            freemarkerConfiguration = FreemarkerConfiguration(
                geminioRecipe.freemarkerTemplatesRootDirPath,
            ),
        )

        executeTemplateWithLoader(
            project = project,
            actionEvent = actionEvent,
            actionText = actionText,
            geminioRecipe = geminioRecipe,
            executionRequest = executionRequest,
        )
    }

    private fun executeTemplateWithLoader(
        project: Project,
        actionEvent: AnActionEvent,
        actionText: String,
        geminioRecipe: GeminioRecipe,
        executionRequest: GeminioRecipeExecutionRequest,
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
                WriteCommandAction.writeCommandAction(project)
                    .withName("Geminio recipe execution '${geminioRecipe.requiredParams.name}")
                    .withGlobalUndo()
                    .withUndoConfirmationPolicy(UndoConfirmationPolicy.REQUEST_CONFIRMATION)
                    .run<IOException> {
                        GeminioRecipeRunner().run(
                            geminioRecipe = geminioRecipe,
                            request = executionRequest,
                        ).also { result ->
                            GeminioGeneratedFilesPostProcessor.process(
                                project = project,
                                createdFiles = result.createdFiles,
                                filesToOpen = result.filesToOpen,
                            )
                        }
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
