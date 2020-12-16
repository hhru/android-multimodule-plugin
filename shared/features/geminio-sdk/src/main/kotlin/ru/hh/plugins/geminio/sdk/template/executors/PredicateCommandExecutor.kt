package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateBoolean
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData


internal fun RecipeExecutor.execute(
    command: RecipeCommand.Predicate,
    executorData: GeminioRecipeExecutorData
) = with(executorData) {
    val validIfExpression = command.validIf

    println("Predicate command [validIfExpression: $validIfExpression]")
    if (validIfExpression.evaluateBoolean(existingParametersMap)) {
        println("\tStart executing commands [validIf == true]")
        executeCommands(
            commands = command.commands,
            executorData = executorData
        )
    } else {
        // Skip predicate command
        println("\tSkip commands execution [validIf == false]")
    }
}