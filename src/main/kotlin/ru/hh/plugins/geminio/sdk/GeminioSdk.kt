package ru.hh.plugins.geminio.sdk

import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe

/**
 * SDK with Geminio functionality.
 */
interface GeminioSdk {

    fun parseYamlRecipe(recipeFilePath: String): GeminioRecipe
}
