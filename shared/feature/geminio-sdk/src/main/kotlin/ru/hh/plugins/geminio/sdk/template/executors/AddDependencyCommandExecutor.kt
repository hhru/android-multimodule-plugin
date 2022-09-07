package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import ru.hh.plugins.logger.HHLogger

internal fun RecipeExecutor.execute(
    command: RecipeCommand.AddDependencies,
    executorData: GeminioRecipeExecutorData
) {
    HHLogger.d("AddDependencies command [$command], isDryRun: ${executorData.isDryRun}")
    if (executorData.isDryRun) {
        HHLogger.d("\tExecute only when isDryRun == true")

        val rootDir = executorData.moduleTemplateData.rootDir.toPsiDirectory(executorData.project)

        BuildGradleModificationService.getInstance(executorData.project)
            .addDepsInModuleDirectory(rootDir, command.dependencies)
    }
}
