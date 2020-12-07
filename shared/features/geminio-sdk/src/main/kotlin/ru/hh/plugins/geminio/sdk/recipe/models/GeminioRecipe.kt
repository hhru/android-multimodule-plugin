package ru.hh.plugins.geminio.sdk.recipe.models


/**
 * Recipe data for building template's  UI and creating files.
 */
data class GeminioRecipe(
    val freemarkerTemplatesRootDirPath: String,
    val requiredParams: RequiredParams,
    val optionalParams: OptionalParams?,
    val recipeParameters: List<RecipeParameter>,
    val recipeCommands: List<RecipeCommand>
)