package ru.hh.plugins.geminio.sdk.recipe.models.expressions


internal sealed class RecipeExpressionCommand {

    data class Fixed(
        val value: String
    ) : RecipeExpressionCommand()

    data class Dynamic(
        val parameterId: String,
        val modifiers: List<RecipeExpressionModifier>
    ) : RecipeExpressionCommand()

    data class SrcOut(
        val modifiers: List<RecipeExpressionModifier>
    ) : RecipeExpressionCommand()

    data class ResOut(
        val modifiers: List<RecipeExpressionModifier>
    ) : RecipeExpressionCommand()

    object ReturnTrue : RecipeExpressionCommand()

    object ReturnFalse : RecipeExpressionCommand()

}