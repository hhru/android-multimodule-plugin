package ru.hh.plugins.geminio.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.model.GeminioRecipe
import ru.hh.plugins.geminio.model.temp_data.GeminioRecipeExecutorData


fun RecipeExecutor.execute(
    command: GeminioRecipe.RecipeCommand.InstantiateAndOpen,
    executorData: GeminioRecipeExecutorData
) {
    execute(
        GeminioRecipe.RecipeCommand.Instantiate(
            from = command.from,
            to = command.to
        ),
        executorData
    )

    execute(
        GeminioRecipe.RecipeCommand.Open(
            file = command.to
        ),
        executorData
    )
}