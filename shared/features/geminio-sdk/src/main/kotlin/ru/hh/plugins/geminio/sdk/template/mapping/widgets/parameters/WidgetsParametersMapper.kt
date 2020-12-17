package ru.hh.plugins.geminio.sdk.template.mapping.widgets.parameters

import ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.models.GeminioTemplateParameterData


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.widgets.RecipeParameter]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter].
 */
internal fun RecipeParameter.toGeminioTemplateParameterData(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): GeminioTemplateParameterData {
    return when (this) {
        is RecipeParameter.StringParameter -> this.toGeminioTemplateParameterData(existingParametersMap)
        is RecipeParameter.BooleanParameter -> this.toGeminioTemplateParameterData(existingParametersMap)
    }
}