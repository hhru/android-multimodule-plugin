package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.evaluateString
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import java.io.File


fun RecipeExecutor.execute(
    command: RecipeCommand.Instantiate,
    executorData: GeminioRecipeExecutorData
) = with(executorData) {
    val from = command.from.evaluateString(moduleTemplateData, existingParametersMap)
    val to = command.to.evaluateString(moduleTemplateData, existingParametersMap)

    println("Instantiate command [command: $command, from: $from, to: $to]")
    if (from == null || to == null) {
        throw IllegalArgumentException("Cannot evaluate 'from' or 'to' expressions [command: $command, from: $from, to: $to]")
    }
    val fileText = freemarkerConfiguration.resolveTemplate(from, resolvedParamsMap)
    save(fileText, File(to))
}