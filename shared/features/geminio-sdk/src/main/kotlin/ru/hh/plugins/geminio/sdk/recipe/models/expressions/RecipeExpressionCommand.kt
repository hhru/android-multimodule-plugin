package ru.hh.plugins.geminio.sdk.recipe.models.expressions


sealed class RecipeExpressionCommand {

    data class Fixed(
        val value: String
    ) : RecipeExpressionCommand()

    data class Dynamic(
        val parameterId: String,
        val modifiers: List<RecipeExpressionModifier>
    ) : RecipeExpressionCommand()

    object SrcOut : RecipeExpressionCommand()

    object ResOut : RecipeExpressionCommand()

    object ManifestOut : RecipeExpressionCommand()

    object RootOut : RecipeExpressionCommand()

    object ReturnTrue : RecipeExpressionCommand()

    object ReturnFalse : RecipeExpressionCommand()

}