package ru.hh.plugins.geminio.sdk.recipe.models

import ru.hh.plugins.geminio.sdk.recipe.models.commands.RecipeCommandsSection
import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSection
import ru.hh.plugins.geminio.sdk.recipe.models.optional.OptionalParams
import ru.hh.plugins.geminio.sdk.recipe.models.required.RequiredParams
import ru.hh.plugins.geminio.sdk.recipe.models.widgets.WidgetsSection


/**
 * Recipe data for building template's  UI and creating files.
 */
internal data class GeminioRecipe(
    val freemarkerTemplatesRootDirPath: String,
    val requiredParams: RequiredParams,
    val optionalParams: OptionalParams,
    val widgetsSection: WidgetsSection,
    val globalsSection: GlobalsSection?,
    val recipeCommands: RecipeCommandsSection
)