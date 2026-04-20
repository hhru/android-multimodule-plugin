package ru.hh.plugins.geminio.sdk.execution.executors

import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeExecutionRequest
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.logger.HHLogger

/**
 * Executes the `addGradlePlugins` recipe command in the single-pass Geminio runtime.
 */
internal fun RecipeCommand.AddGradlePlugins.execute(
    context: GeminioRecipeEvaluationContext,
    request: GeminioRecipeExecutionRequest,
) {
    HHLogger.d("AddGradlePlugins command [$this]")

    BuildGradleModificationService.getInstance(request.project).addGradlePluginsInModuleDirectory(
        rootDir = context.requireRootDirectory(request),
        pluginsIds = pluginsIds,
        isInWriteCommand = true,
    )
}
