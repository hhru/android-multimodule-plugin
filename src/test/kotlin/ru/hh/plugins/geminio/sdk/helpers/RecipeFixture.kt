package ru.hh.plugins.geminio.sdk.helpers

import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import java.nio.file.Path

internal data class RecipeFixture(
    val rootDir: Path,
    val recipeFile: Path,
    val recipe: GeminioRecipe,
)
