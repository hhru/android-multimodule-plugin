package ru.hh.plugins.geminio.sdk.execution.executors

import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeFileOperations
import ru.hh.plugins.geminio.sdk.execution.evaluateString
import ru.hh.plugins.geminio.sdk.recipe.models.commands.MkDirItem
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import java.io.File

/**
 * Executes the recursive `mkDirs` recipe command.
 */
internal fun RecipeCommand.MkDirs.execute(
    context: GeminioRecipeEvaluationContext,
    fileOperations: GeminioRecipeFileOperations,
) {
    dirs.forEach { rootItem ->
        fileOperations.makeDirectories(
            mkDirItem = rootItem,
            combinedPath = "",
            context = context,
        )
    }
}

private fun GeminioRecipeFileOperations.makeDirectories(
    mkDirItem: MkDirItem,
    combinedPath: String,
    context: GeminioRecipeEvaluationContext,
) {
    val directoryName = requireNotNull(mkDirItem.name.evaluateString(context)) {
        "Recipe execution, 'mkDirs' command: cannot evaluate directory name " +
            "[mkDirItem: $mkDirItem, combinedPath: $combinedPath]"
    }
    val filePath = "$combinedPath/".takeIf { combinedPath.isNotEmpty() }.orEmpty() + directoryName

    createDirectory(File(filePath))

    mkDirItem.subDirs.forEach { subdirectoryItem ->
        makeDirectories(
            mkDirItem = subdirectoryItem,
            combinedPath = filePath,
            context = context,
        )
    }
}
