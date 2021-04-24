package ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals

import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.models.GeminioTemplateParameterData


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter].
 */
internal fun GlobalsSectionParameter.toGeminioTemplateParameterData(
    showHiddenValuesId: String,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): GeminioTemplateParameterData {
    return when (this) {
        is GlobalsSectionParameter.StringParameter -> {
            this.toGeminioTemplateParameterData(showHiddenValuesId, existingParametersMap)
        }

        is GlobalsSectionParameter.BooleanParameter -> {
            this.toGeminioTemplateParameterData(showHiddenValuesId, existingParametersMap)
        }
    }
}