package ru.hh.plugins.geminio.model

import kotlin.reflect.KClass


/**
 * Recipe data for building template's  UI and creating files.
 */
data class GeminioRecipe(
    val requiredParams: RequiredParams,
    val optionalParams: OptionalParams?,
    val recipeParameters: List<RecipeParameter>,
    val recipeCommands: List<RecipeCommand>
) {

    data class RequiredParams(
        val revision: Int,
        val name: String,
        val description: String,
    )

    data class OptionalParams(
        val category: GeminioTemplateCategory,
        val formFactor: GeminioTemplateFormFactor,
        val constraints: List<GeminioTemplateConstraint>,
        val screens: List<GeminioTemplateScreen>,
        val minApi: Int,
        val minBuildApi: Int
    )


    sealed class RecipeParameter {

        abstract val id: String
        abstract val name: String
        abstract val help: String
        abstract val visibilityDeclaration: String?
        abstract val availabilityDeclaration: String?


        data class StringParameter(
            override val id: String,
            override val name: String,
            override val help: String,
            override val visibilityDeclaration: String?,
            override val availabilityDeclaration: String?,
            val default: String?,
            val suggestDeclaration: String?,
            val constraints: List<GeminioStringParameterConstraint>,
        ) : RecipeParameter()

        data class BooleanParameter(
            override val id: String,
            override val name: String,
            override val help: String,
            override val visibilityDeclaration: String?,
            override val availabilityDeclaration: String?,
            val default: Boolean?
        ) : RecipeParameter()

        data class EnumParameter<T : Enum<T>>(
            override val id: String,
            override val name: String,
            override val help: String,
            override val visibilityDeclaration: String?,
            override val availabilityDeclaration: String?,
            val enumClass: KClass<T>,
            val default: T?
        ) : RecipeParameter()

    }


    /**
     * Recipe's commands for execution.
     */
    sealed class RecipeCommand {

        /**
         * Instantiate file [from] path into [to] destination.
         */
        data class Instantiate(
            val from: String,
            val to: String
        ) : RecipeCommand()

        /**
         * Try to open file from [file] path.
         */
        data class Open(
            val file: String
        ) : RecipeCommand()

        /**
         * Command which will be executed only if [validIf] predicate returns true.
         */
        data class Predicate(
            val validIf: String,
            val commands: List<RecipeCommand>
        ) : RecipeCommand()

    }

}