package ru.hh.plugins.geminio.sdk.execution.executors

import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeExecutionRequest
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeFileOperations
import ru.hh.plugins.geminio.sdk.execution.evaluateString
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.logger.HHLogger
import java.io.File

/**
 * Executes the `instantiate` recipe command in the pure Geminio runtime.
 */
internal fun RecipeCommand.Instantiate.execute(
    context: GeminioRecipeEvaluationContext,
    request: GeminioRecipeExecutionRequest,
    fileOperations: GeminioRecipeFileOperations,
) {
    val from = from.evaluateString(context)
    val to = to.evaluateString(context)

    HHLogger.d("Instantiate command [command: $this, from: $from, to: $to]")
    require(from != null && to != null) {
        "Cannot evaluate 'from' or 'to' expressions [command: $this, from: $from, to: $to]"
    }

    val fileText = request.freemarkerConfiguration.resolveTemplate(from, request.templateParameters)
    fileOperations.save(fileText, File(to))
}
