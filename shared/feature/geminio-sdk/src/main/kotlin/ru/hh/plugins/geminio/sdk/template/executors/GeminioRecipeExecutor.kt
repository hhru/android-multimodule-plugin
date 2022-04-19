package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import ru.hh.plugins.utils.kotlin.exhaustive

internal fun RecipeExecutor.executeGeminioRecipe(
    geminioRecipe: GeminioRecipe,
    executorData: GeminioRecipeExecutorData
) {
    executeCommands(geminioRecipe.recipeCommands.commands, executorData)
}

internal fun RecipeExecutor.executeCommands(
    commands: List<RecipeCommand>,
    executorData: GeminioRecipeExecutorData
) {
    commands.forEach { command ->
        command.execute(
            recipeExecutor = this,
            executorData = executorData
        )
    }
}

private fun RecipeCommand.execute(
    recipeExecutor: RecipeExecutor,
    executorData: GeminioRecipeExecutorData
) {
    when (this) {
        is RecipeCommand.Instantiate -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.InstantiateAndOpen -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.Open -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.Predicate -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.AddDependencies -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.MkDirs -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.AddGradlePlugins -> recipeExecutor.execute(this, executorData)
        is RecipeCommand.AddNavigation -> recipeExecutor.execute(this, executorData)
    }.exhaustive
}
