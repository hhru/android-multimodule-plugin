package ru.hh.plugins.geminio.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import ru.hh.plugins.utils.kotlin.exhaustive


fun RecipeExecutor.executeCommands(
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
    }.exhaustive
}