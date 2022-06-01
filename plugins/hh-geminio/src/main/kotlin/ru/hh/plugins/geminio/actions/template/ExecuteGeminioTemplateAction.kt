package ru.hh.plugins.geminio.actions.template

import com.android.tools.idea.model.AndroidModel
import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.services.balloonError
import ru.hh.plugins.geminio.services.balloonInfo
import ru.hh.plugins.geminio.services.templates.ConfigureTemplateParametersStepFactory
import ru.hh.plugins.geminio.util.StudioWizardDialogFactory
import ru.hh.plugins.psi_utils.kotlin.shortReferencesAndReformatWithCodeStyle
import kotlin.system.measureTimeMillis

/**
 * Base action for executing templates from YAML config.
 *
 * This action not registered in plugin.xml, because we create it in runtime.
 */
class ExecuteGeminioTemplateAction(
    private val actionText: String,
    private val actionDescription: String,
    private val geminioRecipePath: String
) : AnAction() {

    companion object {
        private const val COMMAND_NAME = "ExecuteGeminioTemplateActionCommand"
        private const val COMMAND_AFTER_WIZARD_NAME = "ExecuteGeminioTemplateActionCommandAfterWizard"

        private const val WIZARD_TITLE = "Geminio wizard"
    }

    init {
        with(templatePresentation) {
            text = actionText
            description = actionDescription
            isEnabledAndVisible = true
        }
    }

    override fun update(e: AnActionEvent) {
        val dataContext = e.dataContext

        val module = LangDataKeys.MODULE.getData(dataContext)
        val facet = module?.let { AndroidFacet.getInstance(it) }

        e.presentation
            .isEnabledAndVisible = (e.project == null || facet == null || AndroidModel.get(facet) == null).not()
    }

    override fun actionPerformed(e: AnActionEvent) {
        println("Start executing template [$actionText]")

        val geminioSdk = GeminioSdkFactory.createGeminioSdk()
        val geminioRecipe = geminioSdk.parseYamlRecipe(geminioRecipePath)

        val (project, facet) = e.fetchEventData()

        val targetDirectory = e.getTargetDirectory()

        val stepFactory = ConfigureTemplateParametersStepFactory.getInstance(project)
        val stepModel = stepFactory.createFromAndroidFacet(
            commandName = COMMAND_NAME,
            stepTitle = actionText,
            facet = facet,
            targetDirectory = targetDirectory,
            androidStudioTemplate = geminioSdk.createGeminioTemplateData(project, geminioRecipe).androidStudioTemplate
        )

        val wizard = ModelWizard.Builder().addStep(stepModel.configureTemplateParametersStep).build().apply {
            this.addResultListener(object : ModelWizard.WizardListener {
                override fun onWizardFinished(result: ModelWizard.WizardResult) {
                    super.onWizardFinished(result)

                    if (result.isFinished.not()) {
                        project.balloonError(message = "User closed Geminio Template Wizard")
                        return
                    }

                    applyShortenReferencesAndCodeStyle()

                    project.balloonInfo(message = "Finished '$actionText' template execution")
                }

                private fun applyShortenReferencesAndCodeStyle() {
                    measureTimeMillis {
                        project.executeWriteCommand(COMMAND_AFTER_WIZARD_NAME) {
                            stepModel.renderTemplateModel.createdFiles.forEach { file ->
                                val psiFile = file.toPsiFile(project) as? KtFile
                                psiFile?.shortReferencesAndReformatWithCodeStyle()
                            }
                        }
                    }.also { println("Shorten references time: $it ms") }
                }
            })
        }

        val dialog =
            StudioWizardDialogFactory(wizard, WIZARD_TITLE)
                .create(project)
        dialog.show()
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

    private fun AnActionEvent.getTargetDirectory(): VirtualFile {
        val currentVirtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext)

        return when {
            currentVirtualFile == null -> {
                throw IllegalStateException("You should select some file for code generation")
            }

            currentVirtualFile.isDirectory.not() -> {
                // If the user selected a simulated folder entry (eg "Manifests"), there will be no target directory
                currentVirtualFile.parent
            }

            else -> {
                currentVirtualFile
            }
        }
    }

    private data class EventData(
        val project: Project,
        val androidFacet: AndroidFacet
    )
}
