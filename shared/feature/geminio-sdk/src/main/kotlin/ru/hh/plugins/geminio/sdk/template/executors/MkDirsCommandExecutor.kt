package ru.hh.plugins.geminio.sdk.template.executors

import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.openapi.vfs.VirtualFile
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.geminio.sdk.recipe.models.commands.MkDirItem
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateString
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import java.io.File

internal fun RecipeExecutor.execute(
    targetDirectory: VirtualFile,
    command: RecipeCommand.MkDirs,
    executorData: GeminioRecipeExecutorData
) {
    for (rootItem in command.dirs) {
        makeDirectories(
            targetDirectory = targetDirectory,
            mkDirItem = rootItem,
            combinedPath = String.EMPTY,
            executorData = executorData
        )
    }
}

private fun RecipeExecutor.makeDirectories(
    targetDirectory: VirtualFile,
    mkDirItem: MkDirItem,
    combinedPath: String,
    executorData: GeminioRecipeExecutorData
) {
    val filePath = "$combinedPath/".takeIf { combinedPath.isNotEmpty() }.orEmpty() + requireNotNull(
        mkDirItem.name.evaluateString(
            targetDirectory = targetDirectory,
            moduleTemplateData = executorData.moduleTemplateData,
            existingParametersMap = executorData.existingParametersMap
        )
    ) {
        "Recipe execution, 'mkDirs' command: Error with directory name evaluation [mkDirItem: $mkDirItem, combinedPath: $combinedPath, executorData: $executorData]"
    }
    createDirectory(File(filePath))

    for (subdirectoryItem in mkDirItem.subDirs) {
        makeDirectories(
            targetDirectory = targetDirectory,
            mkDirItem = subdirectoryItem,
            combinedPath = filePath,
            executorData = executorData
        )
    }
}
