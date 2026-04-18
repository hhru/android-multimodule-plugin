@file:Suppress("detekt.Indentation")

package ru.hh.plugins.geminio.sdk.recipe.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.createParametersMap
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.toExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ReturnFalse
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ReturnTrue
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateBoolean

internal class GeminioRecipeBooleanExpressionSpec : FreeSpec({

    "Should return 'true'" {
        val given = listOf<RecipeExpressionCommand>(
            ReturnTrue
        ).toExpression()

        given.evaluateBoolean(emptyMap()) shouldBe true
    }

    "Should return 'true' even there are some parameters" {
        val given = listOf<RecipeExpressionCommand>(
            ReturnTrue
        ).toExpression()

        given.evaluateBoolean(createParametersMap()) shouldBe true
    }

    "Should return 'false'" {
        val given = listOf<RecipeExpressionCommand>(
            ReturnFalse
        ).toExpression()

        given.evaluateBoolean(emptyMap()) shouldBe false
    }

    "Should return 'false' even there are some parameters" {
        val given = listOf<RecipeExpressionCommand>(
            ReturnFalse
        ).toExpression()

        given.evaluateBoolean(createParametersMap()) shouldBe false
    }

    "Should read value from parameter" {
        val given = listOf<RecipeExpressionCommand>(
            RecipeExpressionCommand.Dynamic("includeModule", emptyList())
        ).toExpression()

        given.evaluateBoolean(createParametersMap()) shouldBe true
        given.evaluateBoolean(createParametersMap(includeModule = false)) shouldBe false
    }

    "Should throw exceptions if have illegal commands for boolean expression" {
        val given1 = listOf(RecipeExpressionCommand.Fixed("fragment_")).toExpression()
        val given2 = listOf(RecipeExpressionCommand.ResOut).toExpression()
        val given3 = listOf(RecipeExpressionCommand.SrcOut).toExpression()
        val given4 = listOf(RecipeExpressionCommand.ManifestOut).toExpression()
        val given5 = listOf(RecipeExpressionCommand.RootOut).toExpression()

        val ex1 = shouldThrow<IllegalArgumentException> { given1.evaluateBoolean(createParametersMap()) }
        val ex2 = shouldThrow<IllegalArgumentException> { given2.evaluateBoolean(createParametersMap()) }
        val ex3 = shouldThrow<IllegalArgumentException> { given3.evaluateBoolean(createParametersMap()) }
        val ex4 = shouldThrow<IllegalArgumentException> { given4.evaluateBoolean(createParametersMap()) }
        val ex5 = shouldThrow<IllegalArgumentException> { given5.evaluateBoolean(createParametersMap()) }

        ex1.message shouldStartWith "Unexpected command for boolean parameter"
        ex2.message shouldStartWith "Unexpected command for boolean parameter"
        ex3.message shouldStartWith "Unexpected command for boolean parameter"
        ex4.message shouldStartWith "Unexpected command for boolean parameter"
        ex5.message shouldStartWith "Unexpected command for boolean parameter"
    }

    "Should throw exception when there are several commands for boolean expression" {
        val given = listOf<RecipeExpressionCommand>(
            RecipeExpressionCommand.Dynamic("includeModule", emptyList()),
            RecipeExpressionCommand.Dynamic("includeModule", emptyList())
        ).toExpression()

        val ex = shouldThrow<IllegalArgumentException> { given.evaluateBoolean(createParametersMap()) }

        ex.message shouldStartWith "Unexpected commands for boolean parameter evaluation"
    }

    "Should throw exception if trying to evaluate not defined parameter for boolean expression" {
        val command = RecipeExpressionCommand.Dynamic("includeFactory", emptyList())
        val given = listOf<RecipeExpressionCommand>(command).toExpression()

        val ex = shouldThrow<IllegalArgumentException> { given.evaluateBoolean(createParametersMap()) }

        ex.message shouldStartWith "Unknown parameter or not boolean parameter for boolean expression [id: ${command.parameterId}]"
    }
})
