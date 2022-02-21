package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData

internal fun RecipeExecutor.execute(
    command: RecipeCommand.AddDependencies,
    executorData: GeminioRecipeExecutorData
) {
    println("AddDependencies command [$command], isDryRun: ${executorData.isDryRun}")
    if (executorData.isDryRun) {
        println("\tExecute only when isDryRun == true")

        val rootDir = executorData.moduleTemplateData.rootDir.toPsiDirectory(executorData.project)

        BuildGradleModificationService.getInstance(executorData.project)
            .addDepsInModuleDirectory(rootDir, command.dependencies)
    }
}
