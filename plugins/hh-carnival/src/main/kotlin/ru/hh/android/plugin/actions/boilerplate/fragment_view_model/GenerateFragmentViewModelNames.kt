package ru.hh.android.plugin.actions.boilerplate.fragment_view_model

import ru.hh.plugins.extensions.EMPTY

data class GenerateFragmentViewModelNames(
    val packageName: String,
    val viewModelClassName: String,
    val modelsPackageName: String,
    val singleEventClassName: String,
    val uiStateClassName: String,
    val uiStateConverterClassName: String,
    val singleEventClassFQCN: String,
    val uiStateClassFQCN: String,
    val uiStateConverterClassFQCN: String,
    val mviFeatureClassName: String,
    val mviFeatureClassFQCN: String,
    val mviFeatureStateClassFQCN: String,
    val mviFeatureNewsClassFQCN: String,
) {

    companion object {

        fun from(featurePrefix: String, packageName: String): GenerateFragmentViewModelNames {
            val (modelsPackageName, mviFeaturePackageName, mviFeatureElementPackageName) = if (packageName.isNotBlank()) {
                val splitted = packageName.split(".")
                val previous = if (splitted.isNotEmpty()) {
                    packageName.removeSuffix(".${splitted.last()}")
                } else {
                    packageName
                }

                Triple(
                    "$packageName.model.",
                    "$previous.feature.",
                    "$previous.feature.element."
                )
            } else {
                Triple(String.EMPTY, String.EMPTY, String.EMPTY)
            }

            val viewModelClassName = "${featurePrefix}ViewModel"
            val singleEventClassName = "${featurePrefix}Event"
            val uiStateClassName = "${featurePrefix}UiState"
            val uiStateConverterClassName = "${featurePrefix}UiStateConverter"

            val singleEventClassFQCN = "${modelsPackageName}$singleEventClassName"
            val uiStateClassFQCN = "${modelsPackageName}$uiStateClassName"
            val uiStateConverterClassFQCN = "${modelsPackageName}$uiStateConverterClassName"

            val mviFeatureClassName = "${featurePrefix}MviFeature"
            val mviFeatureClassFQCN = "${mviFeaturePackageName}$mviFeatureClassName"
            val mviFeatureStateClassFQCN = "${mviFeatureElementPackageName}${mviFeatureClassName}State"
            val mviFeatureNewsClassFQCN = "${mviFeatureElementPackageName}${mviFeatureClassName}News"

            return GenerateFragmentViewModelNames(
                packageName = packageName,
                viewModelClassName = viewModelClassName,
                modelsPackageName = modelsPackageName.removeSuffix("."),
                singleEventClassName = singleEventClassName,
                uiStateClassName = uiStateClassName,
                uiStateConverterClassName = uiStateConverterClassName,
                singleEventClassFQCN = singleEventClassFQCN,
                uiStateClassFQCN = uiStateClassFQCN,
                uiStateConverterClassFQCN = uiStateConverterClassFQCN,
                mviFeatureClassName = mviFeatureClassName,
                mviFeatureClassFQCN = mviFeatureClassFQCN,
                mviFeatureStateClassFQCN = mviFeatureStateClassFQCN,
                mviFeatureNewsClassFQCN = mviFeatureNewsClassFQCN
            )
        }
    }
}
