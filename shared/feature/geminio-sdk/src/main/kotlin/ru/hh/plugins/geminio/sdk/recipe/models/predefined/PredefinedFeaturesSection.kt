package ru.hh.plugins.geminio.sdk.recipe.models.predefined

data class PredefinedFeaturesSection(
    val features: Map<PredefinedFeature, PredefinedFeatureParameter>
) {
    companion object
}
