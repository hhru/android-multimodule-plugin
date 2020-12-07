package ru.hh.plugins.geminio.sdk.model.recipe

import ru.hh.plugins.geminio.sdk.model.enums.GeminioTemplateCategory
import ru.hh.plugins.geminio.sdk.model.enums.GeminioTemplateConstraint
import ru.hh.plugins.geminio.sdk.model.enums.GeminioTemplateFormFactor
import ru.hh.plugins.geminio.sdk.model.enums.GeminioTemplateScreen


data class OptionalParams(
    val revision: Int,
    val category: GeminioTemplateCategory,
    val formFactor: GeminioTemplateFormFactor,
    val constraints: List<GeminioTemplateConstraint>,
    val screens: List<GeminioTemplateScreen>,
    val minApi: Int,
    val minBuildApi: Int
)