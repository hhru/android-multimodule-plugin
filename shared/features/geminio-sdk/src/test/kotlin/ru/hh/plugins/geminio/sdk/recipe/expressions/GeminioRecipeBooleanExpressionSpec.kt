package ru.hh.plugins.geminio.sdk.recipe.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.createParametersMap
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.toExpression
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeExpression.Command
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeExpression.Command.ReturnFalse
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeExpression.Command.ReturnTrue
import ru.hh.plugins.geminio.sdk.template.mapping.evaluateBoolean


class GeminioRecipeBooleanExpressionSpec : FreeSpec({

    "Should return 'true'" {
        val given = listOf<Command>(
            ReturnTrue
        ).toExpression()

        given.evaluateBoolean(emptyMap()) shouldBe true
    }

    "Should return 'true' even there are some parameters" {
        val given = listOf<Command>(
            ReturnTrue
        ).toExpression()

        given.evaluateBoolean(createParametersMap()) shouldBe true
    }

    "Should return 'false'" {
        val given = listOf<Command>(
            ReturnFalse
        ).toExpression()

        given.evaluateBoolean(emptyMap()) shouldBe false
    }

    "Should return 'false' even there are some parameters" {
        val given = listOf<Command>(
            ReturnFalse
        ).toExpression()

        given.evaluateBoolean(createParametersMap()) shouldBe false
    }

    "Should read value from parameter" {
        val given = listOf<Command>(
            Command.Dynamic("includeModule", emptyList())
        ).toExpression()

        given.evaluateBoolean(createParametersMap()) shouldBe true
        given.evaluateBoolean(createParametersMap(includeModule = false)) shouldBe false
    }

    "Should throw exceptions if have illegal commands for boolean expression" {
        val given1 = listOf(Command.Fixed("fragment_")).toExpression()
        val given2 = listOf(Command.ResOut(emptyList())).toExpression()
        val given3 = listOf(Command.SrcOut(emptyList())).toExpression()

        val ex1 = shouldThrow<IllegalArgumentException> { given1.evaluateBoolean(createParametersMap()) }
        val ex2 = shouldThrow<IllegalArgumentException> { given2.evaluateBoolean(createParametersMap()) }
        val ex3 = shouldThrow<IllegalArgumentException> { given3.evaluateBoolean(createParametersMap()) }

        ex1.message shouldStartWith "Unexpected command for boolean parameter"
        ex2.message shouldStartWith "Unexpected command for boolean parameter"
        ex3.message shouldStartWith "Unexpected command for boolean parameter"
    }

    "Should throw exception when there are several commands for boolean expression" {
        val given = listOf<Command>(
            Command.Dynamic("includeModule", emptyList()),
            Command.Dynamic("includeModule", emptyList())
        ).toExpression()

        val ex = shouldThrow<IllegalArgumentException> { given.evaluateBoolean(createParametersMap()) }

        ex.message shouldStartWith "Unexpected commands for boolean parameter evaluation"
    }

    "Should throw exception if trying to evaluate not defined parameter for boolean expression" {
        val command = Command.Dynamic("includeFactory", emptyList())
        val given = listOf<Command>(command).toExpression()

        val ex = shouldThrow<IllegalArgumentException> { given.evaluateBoolean(createParametersMap()) }

        ex.message shouldStartWith "Unknown parameter or not boolean parameter for boolean expression [id: ${command.parameterId}]"
    }

})
