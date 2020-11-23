package ru.hh.plugins.geminio.model

import ru.hh.plugins.geminio.model.enums.GeminioRecipeExpressionModifier
import ru.hh.plugins.geminio.model.enums.GeminioStringParameterConstraint
import ru.hh.plugins.geminio.model.enums.GeminioTemplateCategory
import ru.hh.plugins.geminio.model.enums.GeminioTemplateConstraint
import ru.hh.plugins.geminio.model.enums.GeminioTemplateFormFactor
import ru.hh.plugins.geminio.model.enums.GeminioTemplateScreen
import kotlin.reflect.KClass


/**
 * Recipe data for building template's  UI and creating files.
 */
data class GeminioRecipe(
    val freemarkerTemplatesRootDirPath: String,
    val requiredParams: RequiredParams,
    val optionalParams: OptionalParams?,
    val recipeParameters: List<RecipeParameter>,
    val recipeCommands: List<RecipeCommand>
) {

    data class RequiredParams(
        val name: String,
        val description: String,
    )

    data class OptionalParams(
        val revision: Int,
        val category: GeminioTemplateCategory,
        val formFactor: GeminioTemplateFormFactor,
        val constraints: List<GeminioTemplateConstraint>,
        val screens: List<GeminioTemplateScreen>,
        val minApi: Int,
        val minBuildApi: Int
    )


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


    /**
     * Recipe's commands for execution.
     */
    sealed class RecipeCommand {

        /**
         * Instantiate file [from] path into [to] destination.
         */
        data class Instantiate(
            val from: RecipeExpression,
            val to: RecipeExpression
        ) : RecipeCommand()

        /**
         * Try to open file from [file] path.
         */
        data class Open(
            val file: RecipeExpression
        ) : RecipeCommand()

        /**
         * Command which will be executed only if [validIf] predicate returns true.
         */
        data class Predicate(
            val validIf: RecipeExpression,
            val commands: List<RecipeCommand>
        ) : RecipeCommand()

    }

}