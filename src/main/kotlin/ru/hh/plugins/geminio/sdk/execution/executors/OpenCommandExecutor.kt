package ru.hh.plugins.geminio.sdk.execution.executors

import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeFileOperations
import ru.hh.plugins.geminio.sdk.execution.evaluateString
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.logger.HHLogger
import java.io.File

/**
 * Executes the `open` recipe command by recording the target file for a later UI step.
 */
internal fun RecipeCommand.Open.execute(
    context: GeminioRecipeEvaluationContext,
    fileOperations: GeminioRecipeFileOperations,
) {
    val filePath = file.evaluateString(context)

    HHLogger.d("Open command [filePath: $filePath]")
    require(filePath != null) {
        "Cannot find file for Open command [command: $this, evaluated path: $filePath]"
    }

    fileOperations.open(File(filePath))
}
