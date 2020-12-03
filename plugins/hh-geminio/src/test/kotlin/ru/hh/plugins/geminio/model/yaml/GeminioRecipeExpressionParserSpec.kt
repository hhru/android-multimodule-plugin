package ru.hh.plugins.geminio.model.yaml

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.model.GeminioRecipe
import ru.hh.plugins.geminio.model.GeminioRecipe.RecipeExpression.Command.*
import ru.hh.plugins.geminio.model.enums.GeminioRecipeExpressionModifier.*


class GeminioRecipeExpressionParserSpec : FreeSpec({

    val expressionParser = GeminioRecipeExpressionParser()

    fun String.toRecipeExpression(): GeminioRecipe.RecipeExpression {
        return expressionParser.parseExpression(this)
    }

    fun List<GeminioRecipe.RecipeExpression.Command>.intoExpression(): GeminioRecipe.RecipeExpression {
        return GeminioRecipe.RecipeExpression(this)
    }


    "Should return empty object when convert empty string" {
        val givenExpressionString = ""
        val expected = emptyList<GeminioRecipe.RecipeExpression.Command>().intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expected
    }

    "Should return single Fixed command if there is no dynamic parts" {
        val givenExpressionString = "fragment_blank"
        val expected = listOf(
            Fixed("fragment_blank")
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expected
    }

    "Should find only parameterId in dynamic string" {
        val givenExpressionString = "\${className}"
        val expectedExpression = listOf(
            Dynamic(
                parameterId = "className",
                modifiers = emptyList()
            )
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

    "Should find dynamic part with modifiers" {
        val givenExpressionString = "\${className.classToResource().underlinesToCamelCase()}"
        val expectedExpression = listOf(
            Dynamic(
                parameterId = "className",
                modifiers = listOf(
                    CLASS_TO_RESOURCE,
                    UNDERLINES_TO_CAMEL_CASE
                )
            )
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

    "Should change modifiers order according to dynamic part" {
        val givenExpressionString = "\${className.underlinesToCamelCase().classToResource()}"
        val expectedExpression = listOf(
            Dynamic(
                parameterId = "className",
                modifiers = listOf(
                    UNDERLINES_TO_CAMEL_CASE,
                    CLASS_TO_RESOURCE
                )
            )
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

    "Should normally parse string with several dynamic parts" {
        val givenExpressionString = "\${className.classToResource().underlinesToCamelCase()}Module\${className}"
        val expectedExpression = listOf(
            Dynamic(
                parameterId = "className",
                modifiers = listOf(
                    CLASS_TO_RESOURCE,
                    UNDERLINES_TO_CAMEL_CASE
                )
            ),
            Fixed("Module"),
            Dynamic(
                parameterId = "className",
                modifiers = emptyList()
            ),
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

    "Should recognize {resOut} variable into separate command" {
        val givenExpressionString = "\${resOut}/layout/\${fragmentName}.xml"
        val expectedExpression = listOf(
            ResOut(emptyList()),
            Fixed("/layout/"),
            Dynamic(
                parameterId = "fragmentName",
                modifiers = emptyList()
            ),
            Fixed(".xml"),
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

    "Should recognize {srcOut} variable into separate command" {
        val givenExpressionString = "\${srcOut}/di/\${moduleName}.kt"
        val expectedExpression = listOf(
            SrcOut(emptyList()),
            Fixed("/di/"),
            Dynamic(
                parameterId = "moduleName",
                modifiers = emptyList()
            ),
            Fixed(".kt"),
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

    "Should recognize 'true' string as separate command if it is single word" {
        val givenExpressionString = "true"
        val expectedExpression = listOf(
            ReturnTrue
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

    "Should recognize 'false' string as separate command" {
        val givenExpressionString = "false"
        val expectedExpression = listOf(
            ReturnFalse
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

    "Should read 'true' or 'false' as fixed value if there is some other symbols in expression" {
        val givenExpressionStringWithTrue = "\${className}true"
        val expectedExpressionWithTrue = listOf(
            Dynamic(
                parameterId = "className",
                modifiers = emptyList()
            ),
            Fixed("true")
        ).intoExpression()

        val givenExpressionStringWithFalse = "false_\${className}_false"
        val expectedExpressionWithFalse = listOf(
            Fixed("false_"),
            Dynamic(
                parameterId = "className",
                modifiers = emptyList()
            ),
            Fixed("_false")
        ).intoExpression()

        givenExpressionStringWithTrue.toRecipeExpression() shouldBe expectedExpressionWithTrue
        givenExpressionStringWithFalse.toRecipeExpression() shouldBe expectedExpressionWithFalse
    }

    "Should skip unknown modifiers" {
        val givenExpressionString = "\${resOut.unknown()}/layout/\${fragmentName}.xml"
        val expectedExpression = listOf(
            ResOut(emptyList()),
            Fixed("/layout/"),
            Dynamic(
                parameterId = "fragmentName",
                modifiers = emptyList()
            ),
            Fixed(".xml"),
        ).intoExpression()

        givenExpressionString.toRecipeExpression() shouldBe expectedExpression
    }

})


