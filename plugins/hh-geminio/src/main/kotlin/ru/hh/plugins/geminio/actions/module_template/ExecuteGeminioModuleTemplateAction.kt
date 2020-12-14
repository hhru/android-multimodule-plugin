package ru.hh.plugins.geminio.actions.module_template

import com.android.tools.idea.gradle.actions.SyncProjectAction
import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder
import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.code_modification.SettingsGradleModificationService
import ru.hh.plugins.code_modification.models.BuildGradleDependency
import ru.hh.plugins.code_modification.models.BuildGradleDependencyConfiguration
import ru.hh.plugins.extensions.getSelectedPsiElement
import ru.hh.plugins.geminio.actions.module_template.steps.ChooseModulesModelWizardStep
import ru.hh.plugins.geminio.models.GeminioRecipeExecutorModel
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.sdk.recipe.models.extensions.hasFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.services.balloonError
import ru.hh.plugins.geminio.services.balloonInfo
import ru.hh.plugins.geminio.services.templates.ConfigureTemplateParametersStepFactory
import ru.hh.plugins.geminio.services.templates.GeminioRecipeExecutorFactoryService


class ExecuteGeminioModuleTemplateAction : AnAction() {

    companion object {
        private const val COMMAND_RECIPE_EXECUTION = "ExecuteGeminioModuleTemplateAction.RecipeExecution"

        // TODO - fetch from directory name
        private const val MODULE_TYPE_NAME = "Core Module"
        private const val RECIPE_PATH = "/android-style-guide/geminio/modules_templates/Core Module/recipe.yaml"
    }


    override fun actionPerformed(actionEvent: AnActionEvent) {
        println("Start executing template [New module from Geminio]")

        // region TODO - check in `update` method
        val project = actionEvent.project
        if (project == null) {
            println("Project is null -> good bye")
            return
        }
        val selectedPsiElement = actionEvent.getSelectedPsiElement()
        if (selectedPsiElement == null || selectedPsiElement !is PsiDirectory) {
            println("You should select directory for new module")
            return
        }
        // endregion

        val directoryPath = selectedPsiElement.virtualFile.path
        println("Selected directory path: $directoryPath")

        val geminioSdk = GeminioSdkFactory.createGeminioSdk()
        val geminioRecipe = geminioSdk.parseYamlRecipe(project.basePath + RECIPE_PATH)

        check(geminioRecipe.predefinedFeaturesSection.hasFeature(PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS)) {
            "Recipe for module creation should enable '${PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS.yamlKey}' feature. Add 'predefinedFeatures' section with '${PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS.yamlKey}' list item"
        }

        println("Recipe successfully parsed!")

        val geminioTemplateData = geminioSdk.createGeminioTemplateData(project, geminioRecipe)

        val configureTemplateParametersStepFactory = ConfigureTemplateParametersStepFactory.getInstance(project)
        val stepModel = configureTemplateParametersStepFactory.createForNewModule(
            project = project,
            stepTitle = "Create new $MODULE_TYPE_NAME",
            directoryPath = directoryPath,
            defaultPackageName = "ru.hh",   // TODO - fetch from settings
            androidStudioTemplate = geminioTemplateData.androidStudioTemplate
        )
        val chooseModulesStep = ChooseModulesModelWizardStep(
            renderTemplateModel = stepModel.renderTemplateModel,
            stepTitle = "Choose modules",
            project = project,
            isForAppModules = false
        )
        val chooseAppsStep = ChooseModulesModelWizardStep(
            renderTemplateModel = stepModel.renderTemplateModel,
            stepTitle = "Choose applications",
            project = project,
            isForAppModules = true
        )

        val wizard = ModelWizard.Builder()
            .addStep(stepModel.configureTemplateParametersStep)
            .addStep(chooseModulesStep)
            .addStep(chooseAppsStep)
            .build()

        wizard.addResultListener(object : ModelWizard.WizardListener {
            override fun onWizardFinished(result: ModelWizard.WizardResult) {
                super.onWizardFinished(result)

                if (result.isFinished.not()) {
                    project.balloonError(message = "User closed Geminio Module Template Wizard")
                    return
                }

                val recipeExecutorFactoryService = GeminioRecipeExecutorFactoryService.getInstance(project)
                val recipeExecutorModel = recipeExecutorFactoryService.createRecipeExecutor(
                    project = project,
                    newModuleRootDirectoryPath = directoryPath,
                    geminioTemplateData = geminioTemplateData
                )

                propagateAdditionalParams()

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

                SyncProjectAction().actionPerformed(actionEvent)

                project.balloonInfo(message = "Finished '$MODULE_TYPE_NAME' module template execution")
            }

            override fun onWizardAdvanceError(e: Exception) {
                super.onWizardAdvanceError(e)
                e.printStackTrace()
            }


            private fun propagateAdditionalParams() {
                with(geminioTemplateData) {
                    val librariesModules = chooseModulesStep.getSelectedModules()
                    val applicationModules = chooseAppsStep.getSelectedModules()

                    paramsStore[geminioIds.newApplicationModulesParameterId] = applicationModules.map { it.name }
                    paramsStore[geminioIds.newModuleLibrariesModulesParameterId] = librariesModules.map { it.name }
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

        val dialog = StudioWizardDialogBuilder(wizard, "Geminio Module wizard")
            .setProject(project)
            .build()
        dialog.show()
    }

}