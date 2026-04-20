package ru.hh.plugins.geminio.actions.module_template

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.code_modification.SettingsGradleModificationService
import ru.hh.plugins.extensions.SPACE
import ru.hh.plugins.extensions.UNDERSCORE
import ru.hh.plugins.extensions.getSelectedPsiElement
import ru.hh.plugins.extensions.getTargetDirectory
import ru.hh.plugins.freemarker_wrapper.FreemarkerConfiguration
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_APPLICATIONS_MODULES_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_MODULE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_SOURCE_SET_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.sdk.execution.GeminioGeneratedFilesPostProcessor
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeExecutionRequest
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeRunner
import ru.hh.plugins.geminio.sdk.execution.GeminioTemplateParametersFactory
import ru.hh.plugins.geminio.sdk.form.GeminioForm
import ru.hh.plugins.geminio.sdk.form.GeminioFormSession
import ru.hh.plugins.geminio.sdk.form.toGeminioForm
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.extensions.hasFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter
import ru.hh.plugins.geminio.services.android.GeminioAndroidPathContextFactory
import ru.hh.plugins.geminio.services.android.showAndroidSyncQuestionDialog
import ru.hh.plugins.geminio.wizard.GeminioChooseModulesDialog
import ru.hh.plugins.geminio.wizard.GeminioFormDialog
import ru.hh.plugins.geminio.wizard.GeminioLoadingDialog
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications
import ru.hh.plugins.models.gradle.BuildGradleDependency
import ru.hh.plugins.models.gradle.BuildGradleDependencyConfiguration
import java.awt.Dimension
import java.io.IOException

/**
 * Action for creating new module.
 */
class ExecuteGeminioModuleTemplateAction(
    actionDescription: String,
    private val actionText: String,
    private val geminioRecipePath: String,
) : AnAction(
    /* text = */
    actionText,
    /* description = */
    actionDescription,
    /* icon = */
    null,
) {

    private companion object {
        const val DIALOG_TITLE = "Geminio Module wizard"
        const val CHOOSE_MODULES_DIALOG_TITLE = "Choose app-modules"
        const val LOADING_TITLE = "Generating Geminio module template"
        const val MODULE_FORM_DIALOG_WIDTH = 760
        const val MODULE_FORM_DIALOG_HEIGHT = 620
        const val CHOOSE_MODULES_DIALOG_WIDTH = 720
        const val CHOOSE_MODULES_DIALOG_HEIGHT = 560
    }

    private val moduleFormDialogSize = Dimension(MODULE_FORM_DIALOG_WIDTH, MODULE_FORM_DIALOG_HEIGHT)
    private val chooseModulesDialogSize = Dimension(
        CHOOSE_MODULES_DIALOG_WIDTH,
        CHOOSE_MODULES_DIALOG_HEIGHT,
    )

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        super.update(e)

        val selectedPsiElement = e.getSelectedPsiElement()
        e.presentation.isEnabledAndVisible =
            (e.project == null || selectedPsiElement == null || selectedPsiElement !is PsiDirectory).not()
    }

    override fun actionPerformed(actionEvent: AnActionEvent) {
        HHLogger.d("Start executing template '$actionText'")

        val project = actionEvent.project!!
        val selectedPsiElement = actionEvent.getSelectedPsiElement() as PsiDirectory

        val directoryPath = selectedPsiElement.virtualFile.path
        HHLogger.d("Selected directory path: $directoryPath")

        val geminioSdk = GeminioSdkFactory.createGeminioSdk()
        val geminioRecipe = geminioSdk.parseYamlRecipe(geminioRecipePath)

        check(geminioRecipe.predefinedFeaturesSection.hasFeature(PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS)) {
            "Recipe for module creation should enable " +
                "'${PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS.yamlKey}' feature. " +
                "Add 'predefinedFeatures' section with " +
                "'${PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS.yamlKey}' list item"
        }

        HHLogger.d("Recipe successfully parsed!")

        val targetDirectory = actionEvent.getTargetDirectory()
        val features = geminioRecipe
            .predefinedFeaturesSection
            .features[PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS]
            as PredefinedFeatureParameter.ModuleCreationParameter

        val form = geminioRecipe.toGeminioForm()
        val formSession = GeminioFormSession(form)

        // Delay dialog creation to the next EDT turn so the project-view popup can close first.
        // On macOS/JBR this noticeably reduces native text-input crashes when a modal dialog with
        // focused text fields is opened directly from a popup action.
        ApplicationManager.getApplication().invokeLater {
            showModuleCreationFlow(
                ModuleCreationFlowContext(
                    actionEvent = actionEvent,
                    project = project,
                    directoryPath = directoryPath,
                    targetDirectory = targetDirectory,
                    geminioRecipe = geminioRecipe,
                    features = features,
                    form = form,
                    formSession = formSession,
                ),
            )
        }
    }

    private fun showModuleCreationFlow(context: ModuleCreationFlowContext) {
        HHLogger.d("Showing custom Geminio module form dialog")
        val formDialog = GeminioFormDialog(
            project = context.project,
            title = "$DIALOG_TITLE: $actionText",
            templateName = context.geminioRecipe.requiredParams.name,
            templateDescription = context.geminioRecipe.requiredParams.description,
            form = context.form,
            session = context.formSession,
            confirmActionText = if (context.features.enableChooseModulesStep) "Next" else "Finish",
            preferredScrollSize = moduleFormDialogSize,
            preferInitialInputFocus = false,
        )
        if (formDialog.showAndGet().not()) {
            HHNotifications.error(message = "User closed Geminio Module Template Wizard")
            return
        }

        val selectedApplicationModules = chooseApplicationModules(context)
            ?: return

        executeModuleTemplateWithLoader(
            buildExecutionContext(context, selectedApplicationModules),
        )
    }

    private fun chooseApplicationModules(
        context: ModuleCreationFlowContext,
    ): List<Module>? {
        return if (context.features.enableChooseModulesStep) {
            HHLogger.d("Showing Geminio choose-modules dialog")
            val chooseModulesDialog = GeminioChooseModulesDialog(
                project = context.project,
                title = CHOOSE_MODULES_DIALOG_TITLE,
                confirmActionText = "Finish",
                preferredDialogSize = chooseModulesDialogSize,
            )
            if (chooseModulesDialog.showAndGet().not()) {
                HHNotifications.error(message = "User closed Geminio Module Template Wizard")
                null
            } else {
                chooseModulesDialog.getSelectedModules()
            }
        } else {
            emptyList()
        }
    }

    private fun executeModuleTemplateWithLoader(context: ModuleExecutionContext) {
        val loadingDialog = GeminioLoadingDialog(
            project = context.project,
            title = LOADING_TITLE,
            description = "Generating '$actionText' module template. Please wait...",
        )
        loadingDialog.isVisible = true

        ApplicationManager.getApplication().invokeLater {
            var completedSuccessfully = false
            try {
                WriteCommandAction.writeCommandAction(context.project)
                    .withName("Geminio module recipe execution '${context.geminioRecipe.requiredParams.name}")
                    .withGlobalUndo()
                    .withUndoConfirmationPolicy(UndoConfirmationPolicy.REQUEST_CONFIRMATION)
                    .run<IOException> {
                        GeminioRecipeRunner().run(
                            geminioRecipe = context.geminioRecipe,
                            request = context.executionRequest,
                        ).also { result ->
                            GeminioGeneratedFilesPostProcessor.process(
                                project = context.project,
                                createdFiles = result.createdFiles,
                                filesToOpen = result.filesToOpen,
                            )
                        }

                        modifySettingGradle(
                            project = context.project,
                            directoryPath = context.directoryPath,
                            moduleName = context.moduleName,
                        )
                        modifyBuildGradle(
                            project = context.project,
                            applicationModules = context.applicationModules,
                            moduleName = context.moduleName,
                        )
                    }

                completedSuccessfully = true
            } catch (error: IOException) {
                handleModuleExecutionError(error)
            } catch (error: IllegalArgumentException) {
                handleModuleExecutionError(error)
            } catch (error: IllegalStateException) {
                handleModuleExecutionError(error)
            } finally {
                loadingDialog.dispose()
            }

            if (completedSuccessfully) {
                context.project.showAndroidSyncQuestionDialog(syncPerformedActionEvent = context.actionEvent)
                HHNotifications.info(message = "Finished '$actionText' module template execution")
            }
        }
    }

    private fun handleModuleExecutionError(error: Exception) {
        HHLogger.e("Module template '$actionText' execution failed:\n${error.stackTraceToString()}")
        HHNotifications.error(
            message = "Some error occurred when '$actionText' executed. " +
                "Check warnings at the bottom right corner."
        )
    }

    private fun buildExecutionContext(
        context: ModuleCreationFlowContext,
        selectedApplicationModules: List<Module>,
    ): ModuleExecutionContext {
        val moduleName = requireNotNull(context.formSession.stringValue(FEATURE_MODULE_NAME_PARAMETER_ID)) {
            "Module name should be available for Geminio module template execution"
        }
        val packageName = requireNotNull(context.formSession.stringValue(FEATURE_PACKAGE_NAME_PARAMETER_ID)) {
            "Package name should be available for Geminio module template execution"
        }
        val sourceSet = requireNotNull(context.formSession.stringValue(FEATURE_SOURCE_SET_PARAMETER_ID)) {
            "Source set should be available for Geminio module template execution"
        }
        val sourceCodeFolderName =
            requireNotNull(context.formSession.stringValue(FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID)) {
                "Source code folder name should be available for Geminio module template execution"
            }
        val pathContext = GeminioAndroidPathContextFactory.createForNewModule(
            request = GeminioAndroidPathContextFactory.NewModulePathRequest(
                currentDirPath = context.targetDirectory.path,
                newModuleRootDirectoryPath = context.directoryPath,
                moduleName = moduleName,
                packageName = packageName,
                sourceSet = sourceSet,
                sourceCodeFolderName = sourceCodeFolderName,
            ),
        )
        val additionalParameters = createAdditionalTemplateParameters(
            project = context.project,
            applicationModules = selectedApplicationModules,
        )
        val executionRequest = GeminioRecipeExecutionRequest(
            project = context.project,
            pathContext = pathContext,
            templateParameters = GeminioTemplateParametersFactory.create(
                session = context.formSession,
                packageName = packageName,
                applicationPackageName = packageName,
                currentDirPath = requireNotNull(pathContext.currentDirOut),
                additionalParameters = additionalParameters,
            ),
            freemarkerConfiguration = FreemarkerConfiguration(
                context.geminioRecipe.freemarkerTemplatesRootDirPath,
            ),
        )

        return ModuleExecutionContext(
            project = context.project,
            actionEvent = context.actionEvent,
            directoryPath = context.directoryPath,
            geminioRecipe = context.geminioRecipe,
            executionRequest = executionRequest,
            moduleName = moduleName,
            applicationModules = selectedApplicationModules,
        )
    }

    private data class ModuleCreationFlowContext(
        val actionEvent: AnActionEvent,
        val project: Project,
        val directoryPath: String,
        val targetDirectory: VirtualFile,
        val geminioRecipe: GeminioRecipe,
        val features: PredefinedFeatureParameter.ModuleCreationParameter,
        val form: GeminioForm,
        val formSession: GeminioFormSession,
    )

    private data class ModuleExecutionContext(
        val project: Project,
        val actionEvent: AnActionEvent,
        val directoryPath: String,
        val geminioRecipe: GeminioRecipe,
        val executionRequest: GeminioRecipeExecutionRequest,
        val moduleName: String,
        val applicationModules: List<Module>,
    )

    private fun createAdditionalTemplateParameters(
        project: Project,
        applicationModules: List<Module>,
    ): Map<String, Any?> {
        val projectNamePrefix = project.name.replace(Char.SPACE, Char.UNDERSCORE) + "."

        return mapOf(
            FEATURE_APPLICATIONS_MODULES_PARAMETER_ID to applicationModules.map { module ->
                module.name.removePrefix(projectNamePrefix)
            }
        )
    }

    private fun modifySettingGradle(
        project: Project,
        directoryPath: String,
        moduleName: String,
    ) {
        val settingsGradleModificationService = SettingsGradleModificationService.getInstance(project)
        val newModuleRelativePath =
            directoryPath.removePrefix("${project.basePath!!}/") + "/" + moduleName
        settingsGradleModificationService.addGradleModuleDescription(
            moduleName = moduleName,
            moduleRelativePath = newModuleRelativePath,
        )
    }

    private fun modifyBuildGradle(
        project: Project,
        applicationModules: List<Module>,
        moduleName: String,
    ) {
        val buildGradleModificationService = BuildGradleModificationService.getInstance(project)
        val addingDependencies = listOf(
            BuildGradleDependency.Project(
                configuration = BuildGradleDependencyConfiguration.IMPLEMENTATION,
                value = moduleName,
            )
        )
        applicationModules.forEach { appModule ->
            buildGradleModificationService.addDepsIntoModule(appModule, addingDependencies, true)
        }
    }
}
