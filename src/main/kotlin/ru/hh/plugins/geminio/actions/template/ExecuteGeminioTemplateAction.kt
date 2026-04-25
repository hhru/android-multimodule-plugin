package ru.hh.plugins.geminio.actions.template

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import ru.hh.plugins.geminio.ide.extensions.getTargetDirectory
import ru.hh.plugins.geminio.logger.HHLogger
import ru.hh.plugins.geminio.logger.HHNotifications
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.sdk.execution.GeminioGeneratedFilesPostProcessor
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeExecutionRequest
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeRunner
import ru.hh.plugins.geminio.sdk.execution.GeminioTemplateParametersFactory
import ru.hh.plugins.geminio.sdk.form.GeminioFormSession
import ru.hh.plugins.geminio.sdk.form.GeminioStringConstraintValidationContext
import ru.hh.plugins.geminio.sdk.form.toGeminioForm
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.services.android.GeminioAndroidPathContextFactory
import ru.hh.plugins.geminio.services.android.GeminioNamedModuleTemplateContext
import ru.hh.plugins.geminio.services.android.createGeminioNamedModuleTemplateContext
import ru.hh.plugins.geminio.services.android.hasAvailableAndroidTemplateContext
import ru.hh.plugins.geminio.services.android.packageName
import ru.hh.plugins.geminio.services.android.requireAndroidTemplateContext
import ru.hh.plugins.geminio.services.android.showAndroidSyncQuestionDialog
import ru.hh.plugins.geminio.templating.FreemarkerConfiguration
import ru.hh.plugins.geminio.templating.FreemarkerException
import ru.hh.plugins.geminio.wizard.GeminioFormDialog
import ru.hh.plugins.geminio.wizard.GeminioLoadingDialog
import java.io.File
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
        const val EXECUTION_FAILURE_MESSAGE =
            "Some error occurred when '%s' executed. Check warnings at the bottom right corner."
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.hasAvailableAndroidTemplateContext()
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        HHLogger.d("Start executing template [$actionText]")

        val geminioSdk = GeminioSdkFactory.createGeminioSdk()
        val geminioRecipe = geminioSdk.parseYamlRecipe(geminioRecipePath)

        val (project, facet) = actionEvent.requireAndroidTemplateContext()

        val targetDirectory = actionEvent.getTargetDirectory()
        val templateContext = lazy { facet.createGeminioNamedModuleTemplateContext(targetDirectory) }
        val form = geminioRecipe.toGeminioForm()
        val formSession = GeminioFormSession(form)

        val dialog = GeminioFormDialog(
            project = project,
            title = "$DIALOG_TITLE: $actionText",
            templateName = geminioRecipe.requiredParams.name,
            templateDescription = geminioRecipe.requiredParams.description,
            form = form,
            session = formSession,
            validationContextProvider = {
                createTemplateValidationContext(
                    templateContext = templateContext.value,
                    sourceRoots = ModuleRootManager.getInstance(facet.module)
                        .sourceRoots
                        .map { sourceRoot -> File(sourceRoot.path) },
                )
            },
        )
        if (dialog.showAndGet().not()) {
            return
        }

        val targetPackageName = if (form.fieldsById.containsKey(FEATURE_PACKAGE_NAME_PARAMETER_ID)) {
            formSession.stringValue(FEATURE_PACKAGE_NAME_PARAMETER_ID).orEmpty()
        } else {
            templateContext.value.initialPackageSuggestion.ifBlank { facet.packageName }
        }
        val pathContext = GeminioAndroidPathContextFactory.createForExistingModule(
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
            } catch (error: IOException) {
                handleExecutionError(actionText, error)
            } catch (error: FreemarkerException) {
                handleExecutionError(actionText, error)
            } catch (error: IllegalArgumentException) {
                handleExecutionError(actionText, error)
            } catch (error: IllegalStateException) {
                handleExecutionError(actionText, error)
            } finally {
                loadingDialog.dispose()
            }

            if (completedSuccessfully) {
                project.showAndroidSyncQuestionDialog(syncPerformedActionEvent = actionEvent)
                HHNotifications.info(message = "Finished '$actionText' template execution")
            }
        }
    }

    private fun createTemplateValidationContext(
        templateContext: GeminioNamedModuleTemplateContext,
        sourceRoots: List<File>,
    ): GeminioStringConstraintValidationContext {
        val modulePaths = templateContext.namedModuleTemplate.paths
        val fallbackResDirectory = modulePaths.moduleRoot?.resolve("src/main/res")

        return GeminioStringConstraintValidationContext(
            sourceRoots = sourceRoots,
            resourceDirectories = modulePaths.resDirectories.ifEmpty {
                listOfNotNull(fallbackResDirectory)
            },
        )
    }

    private fun handleExecutionError(
        actionText: String,
        error: Exception,
    ) {
        HHLogger.e("Template '$actionText' execution failed:\n${error.stackTraceToString()}")
        HHNotifications.error(message = EXECUTION_FAILURE_MESSAGE.format(actionText))
    }
}
