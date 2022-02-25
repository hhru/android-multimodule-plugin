package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData

internal fun RecipeExecutor.execute(
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
