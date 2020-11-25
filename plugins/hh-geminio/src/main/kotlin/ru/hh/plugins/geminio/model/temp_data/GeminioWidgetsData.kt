package ru.hh.plugins.geminio.model.temp_data

import ru.hh.plugins.geminio.model.aliases.AndroidStudioTemplateParameter


data class GeminioWidgetsData(
    val existingParametersMap: Map<String, AndroidStudioTemplateParameter>,
    val allParameters: List<AndroidStudioTemplateParameter>
)