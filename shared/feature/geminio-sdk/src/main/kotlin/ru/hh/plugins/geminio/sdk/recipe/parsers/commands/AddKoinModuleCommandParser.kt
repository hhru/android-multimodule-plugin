package ru.hh.plugins.geminio.sdk.recipe.parsers.commands

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommand
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression

private const val FILE_NAME = "file"

internal fun Map<String, Any>.toAddKoinModuleCommand(sectionName: String): RecipeCommand.AddKoinModule {
    check(this.isNotEmpty()) {
        ParsersErrorsFactory.sectionErrorMessage(
            sectionName = sectionName,
            message = """
            |Illegal configuration for adding koin module into module list, 
            |section should contain at least single value
            |""".trimMargin()
        )
    }

    print("fileName: " + this[FILE_NAME] as String)
    return RecipeCommand.AddKoinModule(
        fileNameExpression = (this[FILE_NAME] as String).toRecipeExpression(sectionName)
    )
}