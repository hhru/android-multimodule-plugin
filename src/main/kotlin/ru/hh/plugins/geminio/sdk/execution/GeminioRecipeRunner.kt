package ru.hh.plugins.geminio.sdk.execution

import ru.hh.plugins.geminio.sdk.execution.executors.execute
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand

/**
 * Pure Geminio execution engine that interprets recipe commands without Android Studio templates.
 */
internal class GeminioRecipeRunner(
    private val fileOperationsFactory: (project: com.intellij.openapi.project.Project) -> GeminioRecipeFileOperations = ::IdeGeminioRecipeFileOperations,
) {

    fun run(
        geminioRecipe: GeminioRecipe,
        request: GeminioRecipeExecutionRequest,
    ): GeminioRecipeExecutionResult {
        val executionContext = GeminioRecipeEvaluationContext(
            templateParameters = request.templateParameters,
            pathContext = request.pathContext,
        )
        val fileOperations = fileOperationsFactory(request.project)

        executeCommands(
            commands = geminioRecipe.recipeCommands.commands,
            context = executionContext,
            request = request,
            fileOperations = fileOperations,
        )

        return GeminioRecipeExecutionResult(
            createdFiles = fileOperations.createdFiles.toList(),
            filesToOpen = fileOperations.filesToOpen.toList(),
        )
    }

    internal fun executeCommands(
        commands: List<RecipeCommand>,
        context: GeminioRecipeEvaluationContext,
        request: GeminioRecipeExecutionRequest,
        fileOperations: GeminioRecipeFileOperations,
    ) {
        commands.forEach { command ->
            when (command) {
                is RecipeCommand.Instantiate -> command.execute(context, request, fileOperations)
                is RecipeCommand.InstantiateAndOpen -> command.execute(context, request, fileOperations)
                is RecipeCommand.Open -> command.execute(context, request, fileOperations)
                is RecipeCommand.Predicate -> command.execute(this, context, request, fileOperations)
                is RecipeCommand.AddDependencies -> command.execute(context, request)
                is RecipeCommand.MkDirs -> command.execute(context, request, fileOperations)
                is RecipeCommand.AddGradlePlugins -> command.execute(context, request)
            }
        }
    }
}
