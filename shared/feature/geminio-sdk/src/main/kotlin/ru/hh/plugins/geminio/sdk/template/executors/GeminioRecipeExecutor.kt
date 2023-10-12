package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.openapi.vfs.VirtualFile
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import ru.hh.plugins.utils.kotlin.exhaustive

internal fun RecipeExecutor.executeGeminioRecipe(
    targetDirectory: VirtualFile,
    geminioRecipe: GeminioRecipe,
    executorData: GeminioRecipeExecutorData
) {
    executeCommands(targetDirectory, geminioRecipe.recipeCommands.commands, executorData)
}

internal fun RecipeExecutor.executeCommands(
    targetDirectory: VirtualFile,
    commands: List<RecipeCommand>,
    executorData: GeminioRecipeExecutorData
) {
    commands.forEach { command ->
        command.execute(
            targetDirectory = targetDirectory,
            recipeExecutor = this,
            executorData = executorData
        )
    }
}

private fun RecipeCommand.execute(
    targetDirectory: VirtualFile,
    recipeExecutor: RecipeExecutor,
    executorData: GeminioRecipeExecutorData
) {
    when (this) {
        is RecipeCommand.Instantiate -> recipeExecutor.execute(targetDirectory, this, executorData)
        is RecipeCommand.InstantiateAndOpen -> recipeExecutor.execute(
            targetDirectory,
            this,
            executorData
        )

        is RecipeCommand.Open -> recipeExecutor.execute(targetDirectory, this, executorData)
        is RecipeCommand.Predicate -> recipeExecutor.execute(targetDirectory, this, executorData)
        is RecipeCommand.AddDependencies -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.MkDirs -> recipeExecutor.execute(targetDirectory, this, executorData)
        is RecipeCommand.AddGradlePlugins -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.AddDaggerModule -> recipeExecutor.execute(
            targetDirectory,
            this,
            executorData
        )
    }.exhaustive
}
