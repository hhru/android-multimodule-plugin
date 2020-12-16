@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.optional

import ru.hh.plugins.extensions.EMPTY
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

private const val DEFAULT_REVISION_VALUE = 1
private const val DEFAULT_MIN_API_VALUE = 1
private const val DEFAULT_MIN_BUILD_API_VALUE = 1


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.optional.OptionalParams].
 */
internal fun Map<String, Any>.toOptionalParams(): OptionalParams {
    val optionalParamsMap = this[KEY_OPTIONAL_PARAMS_SECTION] as? Map<String, Any>
        ?: return OptionalParams.default()

    val revision = optionalParamsMap[KEY_OPTIONAL_PARAMS_REVISION] as? Int ?: DEFAULT_REVISION_VALUE
    val categoryYamlKey = optionalParamsMap[KEY_OPTIONAL_PARAMS_CATEGORY] as? String ?: String.EMPTY
    val formFactorYamlKey = optionalParamsMap[KEY_OPTIONAL_PARAMS_FORM_FACTOR] as? String ?: String.EMPTY
    val constraintsYamlKeys = optionalParamsMap[KEY_OPTIONAL_PARAMS_CONSTRAINTS] as? List<String>
        ?: emptyList()
    val screensYamlKeys = optionalParamsMap[KEY_OPTIONAL_PARAMS_SCREENS] as? List<String> ?: emptyList()
    val minApiValue = optionalParamsMap[KEY_OPTIONAL_PARAMS_MIN_API] as? Int ?: DEFAULT_MIN_API_VALUE
    val minBuildApiValue = optionalParamsMap[KEY_OPTIONAL_PARAMS_MIN_BUILD_API] as? Int ?: DEFAULT_MIN_BUILD_API_VALUE

    return OptionalParams(
        revision = revision,
        category = categoryYamlKey.toTemplateCategory(),
        formFactor = formFactorYamlKey.toTemplateFormFactor(),
        constraints = constraintsYamlKeys.map { it.toTemplateConstraint() },
        screens = screensYamlKeys.map { it.toTemplateScreen() },
        minApi = minApiValue,
        minBuildApi = minBuildApiValue,
    )
}


private fun OptionalParams.Companion.default(): OptionalParams {
    return OptionalParams(
        revision = DEFAULT_REVISION_VALUE,
        category = TemplateCategory.OTHER,
        formFactor = TemplateFormFactor.GENERIC,
        constraints = emptyList(),
        screens = emptyList(),
        minApi = DEFAULT_MIN_API_VALUE,
        minBuildApi = DEFAULT_MIN_BUILD_API_VALUE
    )
}


private fun String.toTemplateCategory(): TemplateCategory {
    return requireNotNull(TemplateCategory.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = "${KEY_OPTIONAL_PARAMS_SECTION}:${KEY_OPTIONAL_PARAMS_CATEGORY}",
            key = this,
            acceptableValues = TemplateCategory.availableYamlKeys()
        )
    }
}

private fun String.toTemplateFormFactor(): TemplateFormFactor {
    return requireNotNull(TemplateFormFactor.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = "${KEY_OPTIONAL_PARAMS_SECTION}:${KEY_OPTIONAL_PARAMS_FORM_FACTOR}",
            key = this,
            acceptableValues = TemplateFormFactor.availableYamlKeys()
        )
    }
}

private fun String.toTemplateConstraint(): TemplateConstraint {
    return requireNotNull(TemplateConstraint.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = "${KEY_OPTIONAL_PARAMS_SECTION}:${KEY_OPTIONAL_PARAMS_CONSTRAINTS}",
            key = this,
            acceptableValues = TemplateConstraint.availableYamlKeys()
        )
    }
}

private fun String.toTemplateScreen(): TemplateScreen {
    return requireNotNull(TemplateScreen.fromYamlKey(this)) {
        sectionUnknownEnumKeyErrorMessage(
            sectionName = "${KEY_OPTIONAL_PARAMS_SECTION}:${KEY_OPTIONAL_PARAMS_SCREENS}",
            key = this,
            acceptableValues = TemplateScreen.availableYamlKeys()
        )
    }
}