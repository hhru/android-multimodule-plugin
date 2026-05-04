package ru.hh.plugins.geminio.sdk.recipe.models.expressions

sealed class RecipeExpressionCommand {

    data class Fixed(
        val value: String
    ) : RecipeExpressionCommand()

    data class Dynamic(
        val parameterId: String,
        val modifiers: List<RecipeExpressionModifier>
    ) : RecipeExpressionCommand()

    data class EqualTo(
        val parameter: Dynamic,
        val expectedValue: String
    ) : RecipeExpressionCommand()

    data class NotEqualTo(
        val parameter: Dynamic,
        val expectedValue: String
    ) : RecipeExpressionCommand()

    object SrcOut : RecipeExpressionCommand()

    object ResOut : RecipeExpressionCommand()

    object ManifestOut : RecipeExpressionCommand()

    object RootOut : RecipeExpressionCommand()

    object CurrentDirOut : RecipeExpressionCommand()

    object ReturnTrue : RecipeExpressionCommand()

    object ReturnFalse : RecipeExpressionCommand()
}
