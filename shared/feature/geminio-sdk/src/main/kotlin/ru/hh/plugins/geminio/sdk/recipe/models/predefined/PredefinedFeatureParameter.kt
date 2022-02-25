package ru.hh.plugins.geminio.sdk.recipe.models.predefined

sealed class PredefinedFeatureParameter {
    data class ModuleCreationParameter(
        val defaultPackageNamePrefix: String = "ru.hh"
    ) : PredefinedFeatureParameter()
}
