package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.evaluateString
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import java.io.File


fun RecipeExecutor.execute(
    command: RecipeCommand.Open,
    executorData: GeminioRecipeExecutorData
) = with(executorData) {
    val filePath = command.file.evaluateString(moduleTemplateData, existingParametersMap)

    println("Open command [filePath: $filePath]")
    if (filePath == null) {
        throw IllegalArgumentException("Cannot find file for Open command [command: $command, evaluated path: $filePath]")
    }

    open(File(filePath))
}