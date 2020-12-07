package ru.hh.plugins.geminio.model.mapping

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import ru.hh.plugins.geminio.sdk.model.recipe.RecipeExpression.Command
import ru.hh.plugins.geminio.tests_helpers.GeminioExpressionUtils.createModuleTemplateData
import ru.hh.plugins.geminio.tests_helpers.GeminioExpressionUtils.createParametersMap
import ru.hh.plugins.geminio.tests_helpers.GeminioExpressionUtils.toExpression


class GeminioRecipeStringExpressionSpec : FreeSpec({

    "Should return null" {
        val given = listOf<Command>().toExpression()

        given.evaluateString(createModuleTemplateData(), emptyMap()) shouldBe null
    }

    "Should return fixed content" {
        val given = listOf<Command>(
            Command.Fixed("fragment_")
        ).toExpression()

        given.evaluateString(createModuleTemplateData(), emptyMap()) shouldBe "fragment_"
    }

    "Should read dynamic content from parameter" {
        val given = listOf(
            Command.Dynamic("className", emptyList()),
            Command.Fixed("Module")
        ).toExpression()

        given.evaluateString(createModuleTemplateData(), createParametersMap()) shouldBe "BlankFragmentModule"
        given.evaluateString(
            createModuleTemplateData(),
            createParametersMap(className = "Changed")
        ) shouldBe "ChangedModule"
    }

    "Should read 'srcOut' from module data" {
        val given = listOf(
            Command.SrcOut(emptyList()),
            Command.Fixed("Module.kt")
        ).toExpression()

        given.evaluateString(
            createModuleTemplateData(),
            createParametersMap()
        ) shouldBe "/Project/src/main/kotlin/com/example/mylibrary/Module.kt"
    }

    "Should read 'resOut' from module data" {
        val given = listOf(
            Command.ResOut(emptyList()),
            Command.Fixed("layout/fragment_blank.xml")
        ).toExpression()

        given.evaluateString(
            createModuleTemplateData(),
            createParametersMap()
        ) shouldBe "/Project/src/main/res/layout/fragment_blank.xml"
    }

    "Should throw exception if have illegal commands for string expression" {
        val given1 = listOf(Command.ReturnTrue).toExpression()
        val given2 = listOf(Command.ReturnFalse).toExpression()

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
        val command = Command.Dynamic("fragmentName", emptyList())
        val given = listOf(command).toExpression()

        val ex = shouldThrow<IllegalArgumentException> {
            given.evaluateString(createModuleTemplateData(), createParametersMap())
        }

        ex.message shouldStartWith "Unknown parameter or not string parameter for string expression [${command.parameterId}]"
    }

})
