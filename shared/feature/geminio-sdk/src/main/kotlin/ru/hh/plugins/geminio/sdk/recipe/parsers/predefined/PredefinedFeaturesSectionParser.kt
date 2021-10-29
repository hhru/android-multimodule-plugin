@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.predefined

import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage


private const val KEY_PREDEFINED_FEATURES_SECTION = "predefinedFeatures"

private const val KEY_PARAMETER_PREDEFINE_PACKAGE_NAME = "defaultPackageNamePrefix"


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection].
 */
internal fun Map<String, Any>.toPredefinedFeaturesSection(): PredefinedFeaturesSection {
    val featuresSection = this[KEY_PREDEFINED_FEATURES_SECTION]
        ?: return PredefinedFeaturesSection(emptyMap())
    var predefinedFeatures = featuresSection as? List<Map<String, Any?>>
    // If featuresSection is not List<Map<String, Any?>>
    if (predefinedFeatures == null) {
        predefinedFeatures = (featuresSection as? List<String>)
            ?.map { mapOf(it to emptyMap<String, Any>()) }
    }
    // If featuresSection is not List<String> and is not List<Map<String, Any?>>
    if (predefinedFeatures == null) {
        return PredefinedFeaturesSection(emptyMap())
    }

    return PredefinedFeaturesSection(
        features = predefinedFeatures.associate { it.toPredefinedFeatureParameter() }
    )
}

private fun Map<String, Any?>.toPredefinedFeatureParameter(): Pair<PredefinedFeature, PredefinedFeatureParameter> {
    for (feature in PredefinedFeature.values()) {
        val parameterMap = this[feature.yamlKey] as? Map<String, Any>
        if (parameterMap != null) {
            return feature to parameterMap.parseParameter(feature)
        }
    }

    throw IllegalArgumentException(
        sectionUnknownEnumKeyErrorMessage(
            sectionName = KEY_PREDEFINED_FEATURES_SECTION,
            key = "${this.keys}",
            acceptableValues = PredefinedFeature.availableYamlKeys()
        )
    )
}

private fun Map<String, Any>.parseParameter(
    featureType: PredefinedFeature
): PredefinedFeatureParameter {
    return when (featureType) {
        PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS -> {
            val defaultPackageNamePrefix = this[KEY_PARAMETER_PREDEFINE_PACKAGE_NAME] as? String
            if (defaultPackageNamePrefix == null) {
                PredefinedFeatureParameter.ModuleCreationParameter()
            } else {
                PredefinedFeatureParameter.ModuleCreationParameter(
                    defaultPackageNamePrefix = defaultPackageNamePrefix
                )
            }
        }
    }
}
