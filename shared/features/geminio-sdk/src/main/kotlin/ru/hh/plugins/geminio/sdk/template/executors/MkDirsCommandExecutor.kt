package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import ru.hh.plugins.extensions.EMPTY
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
        makeDirectories(rootItem, String.EMPTY, executorData)
    }
}

private fun RecipeExecutor.makeDirectories(
    mkDirItem: MkDirItem,
    combinedPath: String,
    executorData: GeminioRecipeExecutorData
) {
    val filePath = "$combinedPath/".takeIf { combinedPath.isNotEmpty() }.orEmpty() + requireNotNull(
        mkDirItem.name.evaluateString(
            executorData.moduleTemplateData,
            executorData.existingParametersMap
        )
    ) {
        "Recipe execution, 'mkDirs' command: Error with directory name evaluation [mkDirItem: $mkDirItem, combinedPath: $combinedPath, executorData: $executorData]"
    }
    createDirectory(File(filePath))

    for (subdirectoryItem in mkDirItem.subDirs) {
        makeDirectories(subdirectoryItem, filePath, executorData)
    }
}