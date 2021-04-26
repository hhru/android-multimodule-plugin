@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.required

import ru.hh.plugins.geminio.sdk.recipe.models.required.RequiredParams
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.rootSectionErrorMessage
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionRequiredParameterErrorMessage


private const val KEY_REQUIRED_PARAMS_SECTION = "requiredParams"

private const val KEY_REQUIRED_PARAMS_NAME = "name"
private const val KEY_REQUIRED_PARAMS_DESCRIPTION = "description"


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.required.RequiredParams].
 */
internal fun Map<String, Any>.toRequiredParams(): RequiredParams {
    val requiredParamsMap = requireNotNull(this[KEY_REQUIRED_PARAMS_SECTION] as? LinkedHashMap<String, Any>) {
        rootSectionErrorMessage(KEY_REQUIRED_PARAMS_SECTION)
    }

    val name = requireNotNull(requiredParamsMap[KEY_REQUIRED_PARAMS_NAME] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = KEY_REQUIRED_PARAMS_SECTION,
            key = KEY_REQUIRED_PARAMS_NAME
        )
    }
    val description = requireNotNull(requiredParamsMap[KEY_REQUIRED_PARAMS_DESCRIPTION] as? String) {
        sectionRequiredParameterErrorMessage(
            sectionName = KEY_REQUIRED_PARAMS_SECTION,
            key = KEY_REQUIRED_PARAMS_DESCRIPTION
        )
    }

    return RequiredParams(
        name = name,
        description = description
    )
}