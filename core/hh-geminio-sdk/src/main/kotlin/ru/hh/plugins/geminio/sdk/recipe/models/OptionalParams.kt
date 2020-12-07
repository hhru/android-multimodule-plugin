package ru.hh.plugins.geminio.sdk.recipe.models

import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateCategory
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateConstraint
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateFormFactor
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioTemplateScreen


data class OptionalParams(
    val revision: Int,
    val category: GeminioTemplateCategory,
    val formFactor: GeminioTemplateFormFactor,
    val constraints: List<GeminioTemplateConstraint>,
    val screens: List<GeminioTemplateScreen>,
    val minApi: Int,
    val minBuildApi: Int
)