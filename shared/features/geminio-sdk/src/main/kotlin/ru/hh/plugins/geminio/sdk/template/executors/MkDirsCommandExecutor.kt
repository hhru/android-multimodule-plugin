package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.geminio.sdk.recipe.models.commands.MkDirItem
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateString
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import java.io.File


internal fun RecipeExecutor.execute(
    command: RecipeCommand.MkDirs,
    executorData: GeminioRecipeExecutorData
) {
    for (rootItem in command.dirs) {
        makeDirectories(rootItem, executorData)
    }
}

private fun RecipeExecutor.makeDirectories(mkDirItem: MkDirItem, executorData: GeminioRecipeExecutorData) {
    val filePath = requireNotNull(
        mkDirItem.name.evaluateString(
            executorData.moduleTemplateData,
            executorData.existingParametersMap
        )
    ) {
        "Recipe execution, 'mkDirs' command: Error with directory name evaluation [mkDirItem: $mkDirItem, executorData: $executorData]"
    }
    createDirectory(File(filePath))

    for (subdirectoryItem in mkDirItem.subDirs) {
        makeDirectories(subdirectoryItem, executorData)
    }
}