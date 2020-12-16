package ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals

import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter].
 */
internal fun GlobalsSectionParameter.toAndroidStudioTemplateParameter(
    showHiddenValuesId: String,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameter {
    return when (this) {
        is GlobalsSectionParameter.StringParameter -> {
            this.toAndroidStudioTemplateParameter(showHiddenValuesId, existingParametersMap)
        }

        is GlobalsSectionParameter.BooleanParameter -> {
            this.toAndroidStudioTemplateParameter(showHiddenValuesId, existingParametersMap)
        }
    }
}