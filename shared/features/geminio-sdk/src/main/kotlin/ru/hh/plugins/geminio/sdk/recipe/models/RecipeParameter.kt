package ru.hh.plugins.geminio.sdk.recipe.models

import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioStringParameterConstraint
import kotlin.reflect.KClass


sealed class RecipeParameter {

    abstract val id: String
    abstract val name: String
    abstract val help: String
    abstract val visibilityExpression: RecipeExpression?
    abstract val availabilityExpression: RecipeExpression?


    data class StringParameter(
        override val id: String,
        override val name: String,
        override val help: String,
        override val visibilityExpression: RecipeExpression?,
        override val availabilityExpression: RecipeExpression?,
        val default: String?,
        val suggestExpression: RecipeExpression?,
        val constraints: List<GeminioStringParameterConstraint>,
    ) : RecipeParameter()

    data class BooleanParameter(
        override val id: String,
        override val name: String,
        override val help: String,
        override val visibilityExpression: RecipeExpression?,
        override val availabilityExpression: RecipeExpression?,
        val default: Boolean?
    ) : RecipeParameter()

    data class EnumParameter<T : Enum<T>>(
        override val id: String,
        override val name: String,
        override val help: String,
        override val visibilityExpression: RecipeExpression?,
        override val availabilityExpression: RecipeExpression?,
        val enumClass: KClass<T>,
        val default: T?
    ) : RecipeParameter()

}