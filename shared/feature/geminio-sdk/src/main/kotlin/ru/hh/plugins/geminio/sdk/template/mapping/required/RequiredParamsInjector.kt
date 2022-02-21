package ru.hh.plugins.geminio.sdk.template.mapping.required

import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBuilder

/**
 * Injects parameters from [ru.hh.plugins.geminio.sdk.recipe.models.required.RequiredParams] into
 * [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBuilder]
 */
internal fun AndroidStudioTemplateBuilder.injectRequiredParams(recipe: GeminioRecipe) {
    name = recipe.requiredParams.name
    description = recipe.requiredParams.description
}
