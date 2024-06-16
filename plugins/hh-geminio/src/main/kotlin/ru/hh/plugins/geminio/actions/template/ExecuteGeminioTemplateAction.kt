package ru.hh.plugins.geminio.actions.template

import com.android.tools.idea.model.AndroidModel
import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile
import ru.hh.plugins.dialog.sync.showSyncQuestionDialog
import ru.hh.plugins.extensions.getTargetDirectory
import ru.hh.plugins.geminio.sdk.GeminioSdkFactory
import ru.hh.plugins.geminio.services.templates.ConfigureTemplateParametersStepFactory
import ru.hh.plugins.geminio.wizard.StudioWizardDialogFactory
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications
import ru.hh.plugins.psi_utils.kotlin.shortReferencesAndReformatWithCodeStyle
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
        const val COMMAND_NAME = "ExecuteGeminioTemplateActionCommand"
        const val COMMAND_AFTER_WIZARD_NAME = "ExecuteGeminioTemplateActionCommandAfterWizard"

        const val WIZARD_TITLE = "Geminio wizard"
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

        val stepFactory = ConfigureTemplateParametersStepFactory(project)
        val stepModel = stepFactory.createFromAndroidFacet(
            commandName = COMMAND_NAME,
            stepTitle = actionText,
            facet = facet,
            targetDirectory = targetDirectory,
            androidStudioTemplate = geminioSdk.createGeminioTemplateData(
                project = project,
                geminioRecipe = geminioRecipe,
                targetDirectory = targetDirectory
            ).androidStudioTemplate
        )

        val wizard = ModelWizard.Builder().addStep(stepModel.configureTemplateParametersStep).build().apply {
            this.addResultListener(object : ModelWizard.WizardListener {
                override fun onWizardFinished(result: ModelWizard.WizardResult) {
                    super.onWizardFinished(result)

                    if (result.isFinished.not()) {
                        HHNotifications.error(message = "User closed Geminio Template Wizard")
                        return
                    }

                    applyShortenReferencesAndCodeStyle()

                    project.showSyncQuestionDialog(syncPerformedActionEvent = actionEvent)
                    HHNotifications.info(message = "Finished '$actionText' template execution")
                }

                private fun applyShortenReferencesAndCodeStyle() {
                    measureTimeMillis {
                        project.executeWriteCommand(COMMAND_AFTER_WIZARD_NAME) {
                            stepModel.renderTemplateModel.createdFiles.forEach { file ->
                                val psiFile = file.toPsiFile(project) as? KtFile
                                psiFile?.shortReferencesAndReformatWithCodeStyle()
                            }
                        }
                    }.also { HHLogger.d("Shorten references time: $it ms") }
                }
            })
        }

        val dialog = StudioWizardDialogFactory.getWizardBuilder(wizard, WIZARD_TITLE)
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

    private data class EventData(
        val project: Project,
        val androidFacet: AndroidFacet
    )
}
