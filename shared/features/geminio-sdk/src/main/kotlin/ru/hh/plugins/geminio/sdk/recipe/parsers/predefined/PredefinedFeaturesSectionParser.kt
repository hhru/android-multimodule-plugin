@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.predefined

import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage


private const val KEY_PREDEFINED_FEATURES_SECTION = "predefinedFeatures"


/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection].
 */
internal fun Map<String, Any>.toPredefinedFeaturesSection(): PredefinedFeaturesSection {
    val featuresList = this[KEY_PREDEFINED_FEATURES_SECTION] as? List<String>
        ?: return PredefinedFeaturesSection(emptySet())

    return PredefinedFeaturesSection(
        features = featuresList.mapTo(mutableSetOf()) { yamlKey ->
            val result = PredefinedFeature.fromYamlKey(yamlKey)
                ?: throw IllegalArgumentException(
                    sectionUnknownEnumKeyErrorMessage(
                        sectionName = KEY_PREDEFINED_FEATURES_SECTION,
                        key = yamlKey,
                        acceptableValues = PredefinedFeature.availableYamlKeys()
                    )
                )

            result
        }
    )
}
