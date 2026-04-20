package ru.hh.plugins.geminio.sdk.execution.executors

import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeExecutionRequest
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeFileOperations
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand

/**
 * Executes the combined `instantiateAndOpen` recipe command.
 */
internal fun RecipeCommand.InstantiateAndOpen.execute(
    context: GeminioRecipeEvaluationContext,
    request: GeminioRecipeExecutionRequest,
    fileOperations: GeminioRecipeFileOperations,
) {
    RecipeCommand.Instantiate(
        from = from,
        to = to,
    ).execute(context, request, fileOperations)

    RecipeCommand.Open(
        file = to,
    ).execute(context, request, fileOperations)
}
