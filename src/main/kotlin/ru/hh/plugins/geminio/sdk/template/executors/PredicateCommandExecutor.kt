package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.openapi.vfs.VirtualFile
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateBoolean
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import ru.hh.plugins.logger.HHLogger

internal fun RecipeExecutor.execute(
    targetDirectory: VirtualFile,
    command: RecipeCommand.Predicate,
    executorData: GeminioRecipeExecutorData
) = with(executorData) {
    val validIfExpression = command.validIf

    HHLogger.d("Predicate command [validIfExpression: $validIfExpression]")
    if (validIfExpression.evaluateBoolean(existingParametersMap)) {
        HHLogger.d("\tStart executing commands [validIf == true]")
        executeCommands(
            targetDirectory = targetDirectory,
            commands = command.commands,
            executorData = executorData
        )
    } else {
        // Skip predicate command
        HHLogger.d("\tSkip commands execution [validIf == false], try to execute 'elseCommands' if exists")
        executeCommands(
            targetDirectory = targetDirectory,
            commands = command.elseCommands,
            executorData = executorData
        )
    }
}
