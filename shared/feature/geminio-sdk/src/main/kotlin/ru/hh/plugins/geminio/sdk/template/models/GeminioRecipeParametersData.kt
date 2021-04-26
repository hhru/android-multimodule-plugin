package ru.hh.plugins.geminio.sdk.template.models

import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter


internal data class GeminioRecipeParametersData(
    val templateParameters: List<GeminioTemplateParameterData>,
    val existingParametersMap: Map<String, AndroidStudioTemplateParameter>
)