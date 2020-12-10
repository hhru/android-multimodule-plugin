package ru.hh.plugins.geminio.sdk.recipe.models.globals

import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression


sealed class GlobalsSectionParameter {

    abstract val id: String
    abstract val value: RecipeExpression


    data class StringParameter(
        override val id: String,
        override val value: RecipeExpression
    ) : GlobalsSectionParameter()

    data class BooleanParameter(
        override val id: String,
        override val value: RecipeExpression
    ) : GlobalsSectionParameter()

}