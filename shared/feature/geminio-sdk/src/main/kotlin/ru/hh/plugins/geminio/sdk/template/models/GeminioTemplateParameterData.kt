package ru.hh.plugins.geminio.sdk.template.models

import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter


internal data class GeminioTemplateParameterData(
    val parameterId: String,
    val parameter: AndroidStudioTemplateParameter
)