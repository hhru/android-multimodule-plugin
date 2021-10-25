package ru.hh.plugins.geminio.sdk.recipe.models.extensions

import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection


fun PredefinedFeaturesSection.hasFeature(feature: PredefinedFeature): Boolean {
    return when (feature) {
        PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS -> {
            features.find { it is PredefinedFeatureParameter.ModuleCreationParameter } != null
        }
    }
}