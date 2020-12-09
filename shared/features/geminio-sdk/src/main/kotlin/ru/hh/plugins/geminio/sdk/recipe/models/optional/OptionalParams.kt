package ru.hh.plugins.geminio.sdk.recipe.models.optional


internal data class OptionalParams(
    val revision: Int,
    val category: TemplateCategory,
    val formFactor: TemplateFormFactor,
    val constraints: List<TemplateConstraint>,
    val screens: List<TemplateScreen>,
    val minApi: Int,
    val minBuildApi: Int
) {

    companion object

}