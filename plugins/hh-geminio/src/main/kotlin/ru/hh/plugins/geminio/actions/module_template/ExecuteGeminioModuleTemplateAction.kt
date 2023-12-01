package ru.hh.plugins.geminio.actions.module_template

import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.code_modification.SettingsGradleModificationService
import ru.hh.plugins.dialog.sync.showSyncQuestionDialog
import ru.hh.plugins.extensions.SPACE
import ru.hh.plugins.extensions.UNDERSCORE
import ru.hh.plugins.extensions.getSelectedPsiElement
import ru.hh.plugins.extensions.getTargetDirectory
import ru.hh.plugins.geminio.actions.module_template.steps.ChooseModulesModelWizardStep
import ru.hh.plugins.geminio.models.GeminioRecipeExecutorModel
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.sdk.recipe.models.extensions.hasFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.services.templates.ConfigureTemplateParametersStepFactory
import ru.hh.plugins.geminio.services.templates.GeminioRecipeExecutorFactoryService
import ru.hh.plugins.geminio.wizard.StudioWizardDialogFactory
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications
import ru.hh.plugins.models.gradle.BuildGradleDependency
import ru.hh.plugins.models.gradle.BuildGradleDependencyConfiguration

/**
 * Action for creating new module.
 */
class ExecuteGeminioModuleTemplateAction(
    private val actionText: String,
    private val actionDescription: String,
    private val geminioRecipePath: String
) : AnAction() {

    companion object {
        private const val COMMAND_RECIPE_EXECUTION = "ExecuteGeminioModuleTemplateAction.RecipeExecution"

        private const val WIZARD_TITLE = "Geminio Module wizard"
    }

    init {
        with(templatePresentation) {
            text = actionText
            description = actionDescription
            isEnabledAndVisible = true
        }
    }

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
            "Recipe for module creation should enable '${PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS.yamlKey}' feature. Add 'predefinedFeatures' section with '${PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS.yamlKey}' list item"
        }

        HHLogger.d("Recipe successfully parsed!")

        val targetDirectory = actionEvent.getTargetDirectory()

        val geminioTemplateData = geminioSdk.createGeminioTemplateData(project, geminioRecipe, targetDirectory)

        val configureTemplateParametersStepFactory = ConfigureTemplateParametersStepFactory(project)
        val stepModel = configureTemplateParametersStepFactory.createForNewModule(
            stepTitle = "Create new $actionText",
            directoryPath = directoryPath,
            defaultPackageName = "ru.hh", // TODO - fetch from settings
            androidStudioTemplate = geminioTemplateData.androidStudioTemplate
        )
        val chooseAppsStep = ChooseModulesModelWizardStep(
            renderTemplateModel = stepModel.renderTemplateModel,
            stepTitle = "Choose app-modules",
            project = project,
            isForAppModules = true
        )

        val wizard = ModelWizard.Builder()
            .addStep(stepModel.configureTemplateParametersStep)
            .addStep(chooseAppsStep)
            .build()

        val dialog = StudioWizardDialogFactory.getWizardBuilder(wizard, WIZARD_TITLE)
            .create(project)

        wizard.addResultListener(object : ModelWizard.WizardListener {
            override fun onWizardFinished(result: ModelWizard.WizardResult) {
                super.onWizardFinished(result)

                if (result.isFinished.not()) {
                    HHNotifications.error(message = "User closed Geminio Module Template Wizard")
                    return
                }

                val recipeExecutorFactoryService = GeminioRecipeExecutorFactoryService(project)
                val recipeExecutorModel = recipeExecutorFactoryService.createRecipeExecutor(
                    newModuleRootDirectoryPath = directoryPath,
                    geminioTemplateData = geminioTemplateData
                )

                propagateAdditionalParams()

                try {
                    project.executeWriteCommand(COMMAND_RECIPE_EXECUTION) {
                        with(recipeExecutorModel) {
                            geminioTemplateData.androidStudioTemplate.recipe.invoke(
                                recipeExecutor,
                                moduleTemplateData
                            )
                        }

                        modifySettingGradle(recipeExecutorModel)
                        modifyBuildGradle(recipeExecutorModel)
                    }

                    project.showSyncQuestionDialog(syncPerformedActionEvent = actionEvent)
                    HHNotifications.info(message = "Finished '$actionText' module template execution")
                } catch (ex: Exception) {
                    ex.printStackTrace()

                    dialog.disposeIfNeeded()
                    dialog.close(1)

                    HHNotifications.error(
                        message = "Some error occurred when '$actionText' executed. " +
                            "Check warnings at the bottom right corner."
                    )
                    throw ex
                }
            }

            override fun onWizardAdvanceError(e: Exception) {
                super.onWizardAdvanceError(e)
                e.printStackTrace()
            }

            private fun propagateAdditionalParams() {
                with(geminioTemplateData) {
                    val applicationModules = chooseAppsStep.getSelectedModules()

                    val projectNamePrefix = project.name.replace(Char.SPACE, Char.UNDERSCORE) + "."
                    paramsStore[geminioIds.newApplicationModulesParameterId] = applicationModules.map { module ->
                        module.name.removePrefix(projectNamePrefix)
                    }
                }
            }

            private fun modifySettingGradle(recipeExecutorModel: GeminioRecipeExecutorModel) {
                val settingsGradleModificationService = SettingsGradleModificationService.getInstance(project)
                val newModuleRelativePath =
                    directoryPath.removePrefix("${project.basePath!!}/") + "/" + recipeExecutorModel.moduleName
                settingsGradleModificationService.addGradleModuleDescription(
                    moduleName = recipeExecutorModel.moduleName,
                    moduleRelativePath = newModuleRelativePath
                )
            }

            private fun modifyBuildGradle(recipeExecutorModel: GeminioRecipeExecutorModel) {
                val buildGradleModificationService = BuildGradleModificationService.getInstance(project)
                val addingDependencies = listOf(
                    BuildGradleDependency.Project(
                        configuration = BuildGradleDependencyConfiguration.IMPLEMENTATION,
                        value = recipeExecutorModel.moduleName
                    )
                )
                for (appModule in chooseAppsStep.getSelectedModules()) {
                    buildGradleModificationService.addDepsIntoModule(appModule, addingDependencies, true)
                }
            }
        })

        dialog.show()
    }
}
