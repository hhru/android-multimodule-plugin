package ru.hh.plugins.geminio.sdk.template.models

import ru.hh.plugins.geminio.model.aliases.AndroidStudioTemplateParameter


data class GeminioIdParameterPair(
    val id: String,
    val parameter: AndroidStudioTemplateParameter
)