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
    command: RecipeCommand.Instantiate,
    executorData: GeminioRecipeExecutorData
) = with(executorData) {
    val from = command.from.evaluateString(targetDirectory, moduleTemplateData, existingParametersMap)
    val to = command.to.evaluateString(targetDirectory, moduleTemplateData, existingParametersMap)

    HHLogger.d("Instantiate command [command: $command, from: $from, to: $to]")
    require(from != null && to != null) {
        "Cannot evaluate 'from' or 'to' expressions [command: $command, from: $from, to: $to]"
    }
    val fileText = freemarkerConfiguration.resolveTemplate(from, resolvedParamsMap)
    save(fileText, File(to))
}
