package ru.hh.plugins.geminio.actions

import com.android.tools.idea.model.AndroidModel
import com.android.tools.idea.npw.model.ProjectSyncInvoker
import com.android.tools.idea.npw.model.RenderTemplateModel
import com.android.tools.idea.npw.project.getModuleTemplates
import com.android.tools.idea.npw.project.getPackageForPath
import com.android.tools.idea.npw.template.ConfigureTemplateParametersStep
import com.android.tools.idea.ui.wizard.StudioWizardDialogBuilder
import com.android.tools.idea.wizard.model.ModelWizard
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.android.facet.AndroidFacet
import ru.hh.plugins.geminio.model.yaml.GeminioRecipeReader
import ru.hh.plugins.geminio.services.balloonInfo
import ru.hh.plugins.geminio.template.geminioTemplate
import java.io.File
import java.io.FileNotFoundException


/**
 * Base action for executing templates from YAML config.
 *
 * This action not registered in plugin.xml, because we create it in runtime.
 */
@Suppress("ComponentNotRegistered")
class ExecuteGeminioTemplateAction(
    private val actionText: String,
    private val actionDescription: String,
    private val geminioRecipePath: String
) : AnAction() {

    companion object {
        private const val COMMAND_NAME = "ExecuteGeminioTemplateActionCommand"
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

        println("Search for recipe file: $geminioRecipePath")
        val recipeFile = File(geminioRecipePath)
        if (recipeFile.exists().not()) {
            println("Recipe file doesn't exists [look into $geminioRecipePath]")
            throw FileNotFoundException("Recipe file doesn't exists [look into $geminioRecipePath]")
        }

        println("Recipe file exists -> need to parse, execute, etc")

        val geminioRecipe = GeminioRecipeReader().parse(geminioRecipePath)

        println("geminio recipe to String:\n $geminioRecipe")
        println("==========")
        println("geminio recipe:\n ${geminioRecipe.toIndentString()}")

        val (project, facet) = e.fetchEventData()

        val targetDirectory = e.getTargetDirectory()
        val moduleTemplates = facet.getModuleTemplates(targetDirectory)
        assert(moduleTemplates.isNotEmpty())

        val initialPackageSuggestion = facet.getPackageForPath(moduleTemplates, targetDirectory).orEmpty()

        // It's ok that everything in IDE is red >_< It's ok only for Android Studio 4.1
        val renderModel = RenderTemplateModel.fromFacet(
            facet,
            initialPackageSuggestion,
            moduleTemplates[0],
            COMMAND_NAME,
            ProjectSyncInvoker.DefaultProjectSyncInvoker(),
            true,
        ).apply {
            newTemplate = geminioTemplate(geminioRecipe)
        }

        val configureTemplateStep = ConfigureTemplateParametersStep(
            model = renderModel,
            title = actionText,
            templates = moduleTemplates
        )

        val wizard = ModelWizard.Builder().addStep(configureTemplateStep).build().apply {
            this.addResultListener(object : ModelWizard.WizardListener {
                override fun onWizardFinished(result: ModelWizard.WizardResult) {
                    super.onWizardFinished(result)
                    if (result.isFinished) {
                        println("FINISHED")
                    }
                }
            })
        }

        val dialog = StudioWizardDialogBuilder(wizard, "Geminio wizard")
            .setProject(project)
            .build()
        dialog.show()

        project.balloonInfo(message = "Finished '$actionText' template execution")
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


private fun Any?.toIndentString(): String {
    val notFancy = toString()
    return buildString(notFancy.length) {
        var indent = 0
        fun StringBuilder.line() {
            appendln()
            repeat(2 * indent) { append(' ') }
        }

        for (char in notFancy) {
            if (char == ' ') continue

            when (char) {
                ')', ']' -> {
                    indent--
                    line()
                }
            }

            if (char == '=') append(' ')
            append(char)
            if (char == '=') append(' ')

            when (char) {
                '(', '[', ',' -> {
                    if (char != ',') indent++
                    line()
                }
            }
        }
    }
}