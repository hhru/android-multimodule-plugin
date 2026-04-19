package ru.hh.plugins.geminio.sdk.recipe.models.commands

import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression

data class MkDirItem(
    val name: RecipeExpression,
    val subDirs: List<MkDirItem>
)
