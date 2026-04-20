@file:Suppress("detekt.Indentation")

package ru.hh.plugins.geminio.sdk.recipe.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.execution.evaluateBoolean
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathContext
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.createParametersMap
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.toExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.EqualTo
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.NotEqualTo
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ReturnFalse
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ReturnTrue
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.CLASS_TO_RESOURCE

internal class GeminioRecipeBooleanExpressionSpec : FreeSpec({

    "Should return 'true'" {
        val given = listOf<RecipeExpressionCommand>(
            ReturnTrue
        ).toExpression()

        given.evaluateBoolean(emptyContext()) shouldBe true
    }

    "Should return 'true' even there are some parameters" {
        val given = listOf<RecipeExpressionCommand>(
            ReturnTrue
        ).toExpression()

        given.evaluateBoolean(context(createParametersMap())) shouldBe true
    }

    "Should return 'false'" {
        val given = listOf<RecipeExpressionCommand>(
            ReturnFalse
        ).toExpression()

        given.evaluateBoolean(emptyContext()) shouldBe false
    }

    "Should return 'false' even there are some parameters" {
        val given = listOf<RecipeExpressionCommand>(
            ReturnFalse
        ).toExpression()

        given.evaluateBoolean(context(createParametersMap())) shouldBe false
    }

    "Should read value from parameter" {
        val given = listOf<RecipeExpressionCommand>(
            RecipeExpressionCommand.Dynamic("includeModule", emptyList())
        ).toExpression()

        given.evaluateBoolean(context(createParametersMap())) shouldBe true
        given.evaluateBoolean(context(createParametersMap(includeModule = false))) shouldBe false
    }

    "Should compare string-like parameter to expected value" {
        val given = listOf<RecipeExpressionCommand>(
            EqualTo(
                parameter = RecipeExpressionCommand.Dynamic("uiFramework", emptyList()),
                expectedValue = "compose",
            )
        ).toExpression()

        given.evaluateBoolean(context(createParametersMap(uiFramework = "compose"))) shouldBe true
        given.evaluateBoolean(context(createParametersMap(uiFramework = "views"))) shouldBe false
    }

    "Should compare modified string-like parameter to expected value" {
        val given = listOf<RecipeExpressionCommand>(
            NotEqualTo(
                parameter = RecipeExpressionCommand.Dynamic(
                    parameterId = "className",
                    modifiers = listOf(CLASS_TO_RESOURCE),
                ),
                expectedValue = "feed",
            )
        ).toExpression()

        given.evaluateBoolean(context(createParametersMap(className = "BlankFragment"))) shouldBe true
        given.evaluateBoolean(context(createParametersMap(className = "FeedFragment"))) shouldBe false
    }

    "Should throw exceptions if have illegal commands for boolean expression" {
        val given1 = listOf(RecipeExpressionCommand.Fixed("fragment_")).toExpression()
        val given2 = listOf(RecipeExpressionCommand.ResOut).toExpression()
        val given3 = listOf(RecipeExpressionCommand.SrcOut).toExpression()
        val given4 = listOf(RecipeExpressionCommand.ManifestOut).toExpression()
        val given5 = listOf(RecipeExpressionCommand.RootOut).toExpression()
        val given6 = listOf(
            EqualTo(
                parameter = RecipeExpressionCommand.Dynamic("includeModule", emptyList()),
                expectedValue = "true",
            )
        ).toExpression()

        val ex1 = shouldThrow<IllegalArgumentException> { given1.evaluateBoolean(context(createParametersMap())) }
        val ex2 = shouldThrow<IllegalArgumentException> { given2.evaluateBoolean(context(createParametersMap())) }
        val ex3 = shouldThrow<IllegalArgumentException> { given3.evaluateBoolean(context(createParametersMap())) }
        val ex4 = shouldThrow<IllegalArgumentException> { given4.evaluateBoolean(context(createParametersMap())) }
        val ex5 = shouldThrow<IllegalArgumentException> { given5.evaluateBoolean(context(createParametersMap())) }
        val ex6 = shouldThrow<IllegalArgumentException> { given6.evaluateBoolean(context(createParametersMap())) }

        ex1.message shouldStartWith "Unexpected command for boolean parameter"
        ex2.message shouldStartWith "Unexpected command for boolean parameter"
        ex3.message shouldStartWith "Unexpected command for boolean parameter"
        ex4.message shouldStartWith "Unexpected command for boolean parameter"
        ex5.message shouldStartWith "Unexpected command for boolean parameter"
        ex6.message shouldStartWith "Unknown parameter or not string parameter for string expression"
    }

    "Should throw exception when there are several commands for boolean expression" {
        val given = listOf<RecipeExpressionCommand>(
            RecipeExpressionCommand.Dynamic("includeModule", emptyList()),
            RecipeExpressionCommand.Dynamic("includeModule", emptyList())
        ).toExpression()

        val ex = shouldThrow<IllegalArgumentException> { given.evaluateBoolean(context(createParametersMap())) }

        ex.message shouldStartWith "Unexpected commands for boolean parameter evaluation"
    }

    "Should throw exception if trying to evaluate not defined parameter for boolean expression" {
        val command = RecipeExpressionCommand.Dynamic("includeFactory", emptyList())
        val given = listOf<RecipeExpressionCommand>(command).toExpression()

        val ex = shouldThrow<IllegalArgumentException> { given.evaluateBoolean(context(createParametersMap())) }

        ex.message shouldStartWith
                "Unknown parameter or not boolean parameter for boolean expression [id: ${command.parameterId}]"
    }
})

private fun context(parameters: Map<String, Any?>): GeminioRecipeEvaluationContext {
    return GeminioRecipeEvaluationContext(
        templateParameters = parameters,
        pathContext = GeminioFormPathContext(),
    )
}

private fun emptyContext(): GeminioRecipeEvaluationContext {
    return context(emptyMap())
}
