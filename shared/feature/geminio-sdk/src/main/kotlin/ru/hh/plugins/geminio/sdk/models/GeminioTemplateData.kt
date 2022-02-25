package ru.hh.plugins.geminio.sdk.models

import ru.hh.plugins.geminio.sdk.GeminioAdditionalParamsStore
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter

data class GeminioTemplateData(
    val existingParametersMap: Map<String, AndroidStudioTemplateParameter>,
    val androidStudioTemplate: AndroidStudioTemplate,
    val geminioIds: GeminioTemplateParametersIds,
    val paramsStore: GeminioAdditionalParamsStore
)
