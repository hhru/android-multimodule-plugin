@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.optional

import ru.hh.plugins.geminio.sdk.recipe.models.optional.OptionalParams
import ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateCategory
import ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateConstraint
import ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateFormFactor
import ru.hh.plugins.geminio.sdk.recipe.models.optional.TemplateScreen
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage

private const val KEY_OPTIONAL_PARAMS_SECTION = "optionalParams"

private const val KEY_OPTIONAL_PARAMS_REVISION = "revision"
private const val KEY_OPTIONAL_PARAMS_CATEGORY = "category"
private const val KEY_OPTIONAL_PARAMS_FORM_FACTOR = "formFactor"
private const val KEY_OPTIONAL_PARAMS_CONSTRAINTS = "constraints"
private const val KEY_OPTIONAL_PARAMS_SCREENS = "screens"
private const val KEY_OPTIONAL_PARAMS_MIN_API = "minApi"
private const val KEY_OPTIONAL_PARAMS_MIN_BUILD_API = "minBuildApi"

private val DEFAULT_OPTIONAL_PARAMS = OptionalParams(
    revision = 1,
    category = TemplateCategory.OTHER,
    formFactor = TemplateFormFactor.GENERIC,
    constraints = emptyList(),
    screens = emptyList(),
    minApi = 1,
    minBuildApi = 1
)

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.optional.OptionalParams].
 */
internal fun Map<String, Any>.toOptionalParams(): OptionalParams {
    val default = DEFAULT_OPTIONAL_PARAMS
    val optionalParamsMap = this[KEY_OPTIONAL_PARAMS_SECTION] as? Map<String, Any>
        ?: return default

    return default.copy(
        revision = optionalParamsMap[KEY_OPTIONAL_PARAMS_REVISION] as? Int
            ?: default.revision,
        category = (optionalParamsMap[KEY_OPTIONAL_PARAMS_CATEGORY] as? String)
            ?.toTemplateCategory()
            ?: default.category,
        formFactor = (optionalParamsMap[KEY_OPTIONAL_PARAMS_FORM_FACTOR] as? String)
            ?.toTemplateFormFactor()
            ?: default.formFactor,
        constraints = (optionalParamsMap[KEY_OPTIONAL_PARAMS_CONSTRAINTS] as? List<String>)
            ?.map { it.toTemplateConstraint() }
            ?: default.constraints,
        screens = (optionalParamsMap[KEY_OPTIONAL_PARAMS_SCREENS] as? List<String>)
            ?.map { it.toTemplateScreen() }
            ?: default.screens,
        minApi = optionalParamsMap[KEY_OPTIONAL_PARAMS_MIN_API] as? Int
            ?: default.minApi,
        minBuildApi = optionalParamsMap[KEY_OPTIONAL_PARAMS_MIN_BUILD_API] as? Int
            ?: default.minBuildApi,
    )
}

private fun String.toTemplateCategory(): TemplateCategory {
    return requireNotNull(TemplateCategory.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = "$KEY_OPTIONAL_PARAMS_SECTION:$KEY_OPTIONAL_PARAMS_CATEGORY",
            key = this,
            acceptableValues = TemplateCategory.availableYamlKeys()
        )
    }
}

private fun String.toTemplateFormFactor(): TemplateFormFactor {
    return requireNotNull(TemplateFormFactor.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = "$KEY_OPTIONAL_PARAMS_SECTION:$KEY_OPTIONAL_PARAMS_FORM_FACTOR",
            key = this,
            acceptableValues = TemplateFormFactor.availableYamlKeys()
        )
    }
}

private fun String.toTemplateConstraint(): TemplateConstraint {
    return requireNotNull(TemplateConstraint.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = "$KEY_OPTIONAL_PARAMS_SECTION:$KEY_OPTIONAL_PARAMS_CONSTRAINTS",
            key = this,
            acceptableValues = TemplateConstraint.availableYamlKeys()
        )
    }
}

private fun String.toTemplateScreen(): TemplateScreen {
    return requireNotNull(TemplateScreen.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = "$KEY_OPTIONAL_PARAMS_SECTION:$KEY_OPTIONAL_PARAMS_SCREENS",
            key = this,
            acceptableValues = TemplateScreen.availableYamlKeys()
        )
    }
}
