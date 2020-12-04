package ru.hh.plugins.geminio.model

import ru.hh.plugins.geminio.model.enums.GeminioRecipeExpressionModifier


data class RecipeExpression(
    val expressionCommands: List<Command>
) {

    sealed class Command {

        data class Fixed(
            val value: String
        ) : Command()

        data class Dynamic(
            val parameterId: String,
            val modifiers: List<GeminioRecipeExpressionModifier>
        ) : Command()

        data class SrcOut(
            val modifiers: List<GeminioRecipeExpressionModifier>
        ) : Command()

        data class ResOut(
            val modifiers: List<GeminioRecipeExpressionModifier>
        ) : Command()

        object ReturnTrue : Command()

        object ReturnFalse : Command()

    }

}