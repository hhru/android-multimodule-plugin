@file:Suppress("UNCHECKED_CAST")

package ru.hh.plugins.geminio.sdk.recipe.parsers.predefined

import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter.ModuleCreationParameter.Companion.DEFAULT_SOURCE_CODE_FOLDER_NAME
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter.ModuleCreationParameter.Companion.DEFAULT_PACKAGE_NAME_PREFIX
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter.ModuleCreationParameter.Companion.DEFAULT_SOURCE_SET_NAME
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage

private const val KEY_PREDEFINED_FEATURES_SECTION = "predefinedFeatures"
private const val KEY_PARAMETER_PREDEFINE_PACKAGE_NAME = "defaultPackageNamePrefix"
private const val KEY_PARAMETER_SOURCE_NAME = "defaultSourceSetName"
private const val KEY_PARAMETER_SOURCE_CODE_FOLDER_NAME = "defaultSourceCodeFolderName"

/**
 * Parser from YAML to [ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection].
 */
internal fun Map<String, Any>.toPredefinedFeaturesSection(): PredefinedFeaturesSection {
    val featuresSection = this[KEY_PREDEFINED_FEATURES_SECTION]
        ?: return PredefinedFeaturesSection(emptyMap())
    val featuresSectionList = featuresSection as? List<*> ?: return PredefinedFeaturesSection(emptyMap())

    val featuresSectionMapList = featuresSectionList.map { feature ->
        when (feature) {
            is String -> { // If feature is just string
                mapOf(feature to emptyMap<String, Any>())
            }

            is Map<*, *> -> { // If feature is map
                feature.filterKeys { it is String } as Map<String, Any?>
            }

            else -> {
                null
            }
        }
    }.filterNotNull()

    return PredefinedFeaturesSection(
        features = featuresSectionMapList.associate { it.toPredefinedFeatureParameter() }
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
            val defaultSourceSet = this[KEY_PARAMETER_SOURCE_NAME] as? String
            val codeFolderName = this[KEY_PARAMETER_SOURCE_CODE_FOLDER_NAME] as? String
            PredefinedFeatureParameter.ModuleCreationParameter(
                defaultPackageNamePrefix = defaultPackageNamePrefix ?: DEFAULT_PACKAGE_NAME_PREFIX,
                defaultSourceSet = defaultSourceSet ?: DEFAULT_SOURCE_SET_NAME,
                defaultSourceCodeFolderName = codeFolderName ?: DEFAULT_SOURCE_CODE_FOLDER_NAME,
            )
        }
    }
}
