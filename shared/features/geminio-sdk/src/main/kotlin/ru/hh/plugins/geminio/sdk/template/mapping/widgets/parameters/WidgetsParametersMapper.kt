package ru.hh.plugins.geminio.sdk.template.mapping.widgets.parameters

import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter].
 */
internal fun RecipeParameter.toAndroidStudioTemplateParameter(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameter {
    return when (this) {
        is RecipeParameter.StringParameter -> this.toAndroidStudioTemplateParameter(existingParametersMap)
        is RecipeParameter.BooleanParameter -> this.toAndroidStudioTemplateParameter(existingParametersMap)
    }
}