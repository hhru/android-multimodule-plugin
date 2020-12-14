package ru.hh.plugins.geminio.actions.module_template

import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder
import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile
import ru.hh.plugins.extensions.getSelectedPsiElement
import ru.hh.plugins.extensions.psi.kotlin.shortReferencesAndReformatWithCodeStyle
import ru.hh.plugins.geminio.services.templates.GeminioRecipeExecutorFactoryService
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.sdk.recipe.models.extensions.hasFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.services.templates.ConfigureTemplateParametersStepFactory
import kotlin.system.measureTimeMillis


class ExecuteGeminioModuleTemplateAction : AnAction() {

    companion object {
        private const val COMMAND_RECIPE_EXECUTION = "ExecuteGeminioModuleTemplateAction.RecipeExecution"
        private const val COMMAND_AFTER_WIZARD = "ExecuteGeminioModuleTemplateAction.AfterWizard"


        // TODO - fetch from directory name
        private const val MODULE_TYPE_NAME = "Core Module"
        private const val RECIPE_PATH = "/android-style-guide/geminio/modules_templates/Core Module/recipe.yaml"
    }


    override fun actionPerformed(e: AnActionEvent) {
        println("Start executing template [New module from Geminio]")

        // region TODO - check in `update` method
        val project = e.project
        if (project == null) {
            println("Project is null -> good bye")
            return
        }
        val selectedPsiElement = e.getSelectedPsiElement()
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
            "Recipe for module creation should enable ${PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS} feature."
        }

        println("Recipe successfully parsed!")

        val geminioTemplateData = geminioSdk.createGeminioTemplateData(project, geminioRecipe)

        val stepFactory = ConfigureTemplateParametersStepFactory.getInstance(project)
        val stepModule = stepFactory.createForNewModule(
            project = project,
            stepTitle = "Create new $MODULE_TYPE_NAME",
            directoryPath = directoryPath,
            defaultPackageName = "ru.hh",   // TODO - fetch from settings
            androidStudioTemplate = geminioTemplateData.androidStudioTemplate
        )


        val wizard = ModelWizard.Builder().addStep(stepModule.configureTemplateParametersStep).build().apply {
            this.addResultListener(object : ModelWizard.WizardListener {
                override fun onWizardFinished(result: ModelWizard.WizardResult) {
                    super.onWizardFinished(result)

                    val recipeExecutorFactoryService = GeminioRecipeExecutorFactoryService.getInstance(project)
                    val recipeExecutorModel = recipeExecutorFactoryService.createRecipeExecutor(
                        project = project,
                        newModuleRootDirectoryPath = directoryPath,
                        geminioTemplateData = geminioTemplateData
                    )

                    project.executeWriteCommand(COMMAND_RECIPE_EXECUTION) {
                        with(recipeExecutorModel) {
                            geminioTemplateData.androidStudioTemplate.recipe.invoke(recipeExecutor, moduleTemplateData)
                        }
                    }

                    applyShortenReferencesAndCodeStyle()
                }

                override fun onWizardAdvanceError(e: Exception) {
                    super.onWizardAdvanceError(e)
                    e.printStackTrace()
                }


                private fun applyShortenReferencesAndCodeStyle() {
                    measureTimeMillis {
                        project.executeWriteCommand(COMMAND_AFTER_WIZARD) {
                            stepModule.renderTemplateModel.createdFiles.forEach { file ->
                                val psiFile = file.toPsiFile(project) as? KtFile
                                psiFile?.shortReferencesAndReformatWithCodeStyle()
                            }
                        }
                    }.also { println("Shorten references time: $it ms") }
                }
            })
        }

        val dialog = StudioWizardDialogBuilder(wizard, "Geminio Module wizard")
            .setProject(project)
            .build()
        dialog.show()
    }

}