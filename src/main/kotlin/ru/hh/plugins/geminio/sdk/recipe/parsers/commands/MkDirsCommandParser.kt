@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.MkDirItem
import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand.MkDirs] command.
 */
internal fun List<Any>.toMkDirsCommand(sectionName: String): RecipeCommand.MkDirs {
    return RecipeCommand.MkDirs(
        dirs = this.parseMkDirsItems(sectionName)
    )
}

private fun List<Any>.parseMkDirsItems(sectionName: String): List<MkDirItem> {
    val items = mutableListOf<MkDirItem>()

    for (listItem in this) {
        items += when (listItem) {
            is String -> {
                MkDirItem(listItem.toRecipeExpression(sectionName), emptyList())
            }

            else -> {
                val map = requireNotNull(listItem as? Map<String, List<Any>>) {
                    sectionErrorMessage(
                        sectionName = sectionName,
                        message = "Can't parse mkDirs command structure item as Map<String, List<Any>>. You should check your yaml config."
                    )
                }

                val dirName = requireNotNull(map.keys.firstOrNull()) {
                    sectionErrorMessage(
                        sectionName = sectionName,
                        message = "We don't allow empty map objects for mkDirs command structure. If you need empty directory, just use simple strings, not objects [`- di`, not `- di:`]"
                    )
                }

                val nextSectionName = "$sectionName:$dirName"
                MkDirItem(
                    name = dirName.toRecipeExpression(nextSectionName),
                    subDirs = map[dirName]!!.parseMkDirsItems(nextSectionName)
                )
            }
        }
    }

    return items
}
