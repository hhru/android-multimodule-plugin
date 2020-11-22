package ru.hh.plugins.geminio.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.model.GeminioRecipe
import ru.hh.plugins.geminio.model.temp_data.GeminioRecipeExecutorData
import ru.hh.plugins.utils.kotlin.exhaustive


fun RecipeExecutor.executeCommands(
    commands: List<GeminioRecipe.RecipeCommand>,
    executorData: GeminioRecipeExecutorData
) {
    commands.forEach { command ->
        command.execute(
            recipeExecutor = this,
            executorData = executorData
        )
    }
}


private fun GeminioRecipe.RecipeCommand.execute(
    recipeExecutor: RecipeExecutor,
    executorData: GeminioRecipeExecutorData
) {
    when (this) {
        is GeminioRecipe.RecipeCommand.Instantiate -> recipeExecutor.execute(this, executorData)
        is GeminioRecipe.RecipeCommand.Open -> recipeExecutor.execute(this, executorData)
        is GeminioRecipe.RecipeCommand.Predicate -> recipeExecutor.execute(this, executorData)
    }.exhaustive
}