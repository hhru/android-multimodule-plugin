package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.openapi.vfs.VirtualFile
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateString
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import ru.hh.plugins.logger.HHLogger
import java.io.File

internal fun RecipeExecutor.execute(
    targetDirectory: VirtualFile,
    command: RecipeCommand.Open,
    executorData: GeminioRecipeExecutorData
) = with(executorData) {
    val filePath = command.file.evaluateString(targetDirectory, moduleTemplateData, existingParametersMap)

    HHLogger.d("Open command [filePath: $filePath]")
    require(filePath != null) {
        "Cannot find file for Open command [command: $command, evaluated path: $filePath]"
    }

    open(File(filePath))
}
