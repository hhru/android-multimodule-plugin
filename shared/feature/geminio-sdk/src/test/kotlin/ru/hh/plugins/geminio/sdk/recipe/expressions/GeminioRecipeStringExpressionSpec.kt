@file:Suppress("detekt.Indentation")

package ru.hh.plugins.geminio.sdk.recipe.expressions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.createModuleTemplateData
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.createParametersMap
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.evaluateString
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.toExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand

internal class GeminioRecipeStringExpressionSpec : FreeSpec({

    "Should return null" {
        val given = listOf<RecipeExpressionCommand>().toExpression()

        given.evaluateString(createModuleTemplateData(), emptyMap()) shouldBe null
    }

    "Should return fixed content" {
        val given = listOf<RecipeExpressionCommand>(
            RecipeExpressionCommand.Fixed("fragment_")
        ).toExpression()

        given.evaluateString(createModuleTemplateData(), emptyMap()) shouldBe "fragment_"
    }

    "Should read dynamic content from parameter" {
        val given = listOf(
            RecipeExpressionCommand.Dynamic("className", emptyList()),
            RecipeExpressionCommand.Fixed("Module")
        ).toExpression()

        given.evaluateString(createModuleTemplateData(), createParametersMap()) shouldBe "BlankFragmentModule"
        given.evaluateString(
            createModuleTemplateData(),
            createParametersMap(className = "Changed")
        ) shouldBe "ChangedModule"
    }

    "Should read 'srcOut' from module data" {
        val given = listOf(
            RecipeExpressionCommand.SrcOut,
            RecipeExpressionCommand.Fixed("Module.kt")
        ).toExpression()

        given.evaluateString(
            createModuleTemplateData(),
            createParametersMap()
        ) shouldBe "/Project/src/main/kotlin/com/example/mylibrary/Module.kt"
    }

    "Should read 'resOut' from module data" {
        val given = listOf(
            RecipeExpressionCommand.ResOut,
            RecipeExpressionCommand.Fixed("layout/fragment_blank.xml")
        ).toExpression()

        given.evaluateString(
            createModuleTemplateData(),
            createParametersMap()
        ) shouldBe "/Project/src/main/res/layout/fragment_blank.xml"
    }

    "Should throw exception if have illegal commands for string expression" {
        val given1 = listOf(RecipeExpressionCommand.ReturnTrue).toExpression()
        val given2 = listOf(RecipeExpressionCommand.ReturnFalse).toExpression()

        val ex1 = shouldThrow<IllegalArgumentException> {
            given1.evaluateString(createModuleTemplateData(), createParametersMap())
        }
        val ex2 = shouldThrow<IllegalArgumentException> {
            given2.evaluateString(createModuleTemplateData(), createParametersMap())
        }

        ex1.message shouldStartWith "Unexpected command for string value"
        ex2.message shouldStartWith "Unexpected command for string value"
    }

    "Should throw exception if there is unknown parameter for string expression" {
        val command = RecipeExpressionCommand.Dynamic("fragmentName", emptyList())
        val given = listOf(command).toExpression()

        val ex = shouldThrow<IllegalArgumentException> {
            given.evaluateString(createModuleTemplateData(), createParametersMap())
        }

        ex.message shouldStartWith "Unknown parameter or not string parameter for string expression [${command.parameterId}]"
    }
})
