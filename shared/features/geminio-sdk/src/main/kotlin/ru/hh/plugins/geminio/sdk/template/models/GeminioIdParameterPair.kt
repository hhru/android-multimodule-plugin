package ru.hh.plugins.geminio.sdk.template.models

import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter


data class GeminioIdParameterPair(
    val id: String,
    val parameter: AndroidStudioTemplateParameter
)