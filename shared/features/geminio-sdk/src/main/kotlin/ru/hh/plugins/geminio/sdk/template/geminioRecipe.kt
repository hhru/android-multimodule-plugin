package ru.hh.plugins.geminio.template

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import ru.hh.plugins.geminio.template.executors.executeCommands


fun RecipeExecutor.geminioRecipe(
    geminioRecipe: GeminioRecipe,
    executorData: GeminioRecipeExecutorData
) {
    executeCommands(geminioRecipe.recipeCommands, executorData)
}




