@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.globals

import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSection
import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter
import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameterType
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression


private const val KEY_GLOBALS_SECTION = "globals"

private const val KEY_PARAMETER_ID = "id"
private const val KEY_PARAMETER_VALUE = "value"


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSection].
 */
internal fun Map<String, Any>.toGlobalsSection(): GlobalsSection {
    val globalsSectionList = this[KEY_GLOBALS_SECTION] as? List<Map<String, Any>>
        ?: return GlobalsSection(emptyList())

    return GlobalsSection(
        parameters = globalsSectionList.map { it.toGlobalsSectionParameter() }
    )
}


private fun Map<String, Any>.toGlobalsSectionParameter(): GlobalsSectionParameter {
    for (parameterType in GlobalsSectionParameterType.values()) {
        val parameterMap = this[parameterType.yamlKey] as? Map<String, Any>
        if (parameterMap != null) {
            return parameterMap.parseParameter(parameterType)
        }
    }

    throw IllegalArgumentException(
        sectionUnknownEnumKeyErrorMessage(
            sectionName = KEY_GLOBALS_SECTION,
            key = "${this.keys}",
            acceptableValues = GlobalsSectionParameterType.availableYamlKeys()
        )
    )
}

private fun Map<String, Any>.parseParameter(
    parameterType: GlobalsSectionParameterType
): GlobalsSectionParameter {
    val id = requireNotNull(this[KEY_PARAMETER_ID] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = KEY_GLOBALS_SECTION,
            key = KEY_PARAMETER_ID,
            additionalInfo = "parameterType: $parameterType"
        )
    }
    val valueExpressionString = requireNotNull(this[KEY_PARAMETER_VALUE] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = KEY_GLOBALS_SECTION,
            key = KEY_PARAMETER_VALUE,
            additionalInfo = "parameterType: $parameterType"
        )
    }

    return when (parameterType) {
        GlobalsSectionParameterType.STRING_PARAMETER -> {
            GlobalsSectionParameter.StringParameter(
                id = id,
                value = valueExpressionString.toRecipeExpression(KEY_GLOBALS_SECTION)
            )
        }

        GlobalsSectionParameterType.BOOLEAN_PARAMETER -> {
            GlobalsSectionParameter.BooleanParameter(
                id = id,
                value = valueExpressionString.toRecipeExpression(KEY_GLOBALS_SECTION)
            )
        }
    }
}