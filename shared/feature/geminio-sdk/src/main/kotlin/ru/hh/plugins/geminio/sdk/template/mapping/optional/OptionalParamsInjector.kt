package ru.hh.plugins.geminio.sdk.template.mapping.optional

import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBuilder


/**
 * Injects parameters from [ru.hh.plugins.geminio.sdk.recipe.models.optional.OptionalParams] into
 * [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBuilder]
 */
internal fun AndroidStudioTemplateBuilder.injectOptionalParams(recipe: GeminioRecipe) {
    with(recipe) {
        revision = optionalParams.revision
        category = optionalParams.category.toAndroidStudioTemplateCategory()
        formFactor = optionalParams.formFactor.toAndroidStudioTemplateFormFactor()
        constraints = optionalParams.constraints.map { it.toAndroidStudioTemplateConstraint() }
        screens = optionalParams.screens.map { it.toAndroidStudioTemplateScreen() }
        minApi = optionalParams.minApi
        minBuildApi = optionalParams.minBuildApi
    }
}