package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory

private const val FILE_NAME = "file"

internal fun Map<String, Any>.toAddNavigationCommand(sectionName: String): RecipeCommand.AddNavigation {
    check(this.isNotEmpty()) {
        ParsersErrorsFactory.sectionErrorMessage(
            sectionName = sectionName,
            message = """
            |Illegal configuration for adding navigation into xml, 
            |section should contain at least single value
            |""".trimMargin()
        )
    }

    print("fileName: " + this[FILE_NAME])
    return RecipeCommand.AddNavigation(
        fileName = this[FILE_NAME] as String
    )
}
