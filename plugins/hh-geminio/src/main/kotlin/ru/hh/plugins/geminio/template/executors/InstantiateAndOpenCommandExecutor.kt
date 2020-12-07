package ru.hh.plugins.geminio.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.model.recipe.RecipeCommand
import ru.hh.plugins.geminio.model.temp_data.GeminioRecipeExecutorData


fun RecipeExecutor.execute(
    command: RecipeCommand.InstantiateAndOpen,
    executorData: GeminioRecipeExecutorData
) {
    execute(
        RecipeCommand.Instantiate(
            from = command.from,
            to = command.to
        ),
        executorData
    )

    execute(
        RecipeCommand.Open(
            file = command.to
        ),
        executorData
    )
}