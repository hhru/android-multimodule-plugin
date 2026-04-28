package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.openapi.vfs.VirtualFile
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData

internal fun RecipeExecutor.execute(
    targetDirectory: VirtualFile,
    command: RecipeCommand.InstantiateAndOpen,
    executorData: GeminioRecipeExecutorData
) {
    execute(
        targetDirectory = targetDirectory,
        command = RecipeCommand.Instantiate(
            from = command.from,
            to = command.to
        ),
        executorData = executorData
    )

    execute(
        targetDirectory = targetDirectory,
        command = RecipeCommand.Open(
            file = command.to
        ),
        executorData = executorData
    )
}
