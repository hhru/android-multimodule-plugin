package ru.hh.plugins.geminio.sdk.execution.executors

import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeExecutionRequest
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeFileOperations
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeRunner
import ru.hh.plugins.geminio.sdk.execution.evaluateBoolean
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.logger.HHLogger

/**
 * Executes the conditional `predicate` recipe command.
 */
internal fun RecipeCommand.Predicate.execute(
    runner: GeminioRecipeRunner,
    context: GeminioRecipeEvaluationContext,
    request: GeminioRecipeExecutionRequest,
    fileOperations: GeminioRecipeFileOperations,
) {
    HHLogger.d("Predicate command [validIfExpression: $validIf]")

    if (validIf.evaluateBoolean(context)) {
        HHLogger.d("\tStart executing commands [validIf == true]")
        runner.executeCommands(commands, context, request, fileOperations)
    } else {
        HHLogger.d("\tSkip commands execution [validIf == false], try to execute 'elseCommands' if exists")
        runner.executeCommands(elseCommands, context, request, fileOperations)
    }
}
