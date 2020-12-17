package ru.hh.plugins.geminio.sdk.recipe.parsers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.Dynamic
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.Fixed
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ResOut
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ReturnFalse
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ReturnTrue
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.SrcOut
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.CLASS_TO_RESOURCE
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.UNDERLINES_TO_CAMEL_CASE
import ru.hh.plugins.geminio.sdk.recipe.parsers.expressions.toRecipeExpression


private const val SECTION_NAME = "Test"


internal class GeminioRecipeExpressionParserSpec : FreeSpec({

    fun List<RecipeExpressionCommand>.intoExpression(): RecipeExpression {
        return RecipeExpression(this)
    }


    "Should return empty object when convert empty string" {
        val givenExpressionString = ""
        val expected = emptyList<RecipeExpressionCommand>().intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expected
    }

    "Should return single Fixed command if there is no dynamic parts" {
        val givenExpressionString = "fragment_blank"
        val expected = listOf(
            Fixed("fragment_blank")
        ).intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expected
    }

    "Should find only parameterId in dynamic string" {
        val givenExpressionString = "\${className}"
        val expectedExpression = listOf(
            Dynamic(
                parameterId = "className",
                modifiers = emptyList()
            )
        ).intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
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

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
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

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
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

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
    }

    "Should recognize {resOut} variable into separate command" {
        val givenExpressionString = "\${resOut}/layout/\${fragmentName}.xml"
        val expectedExpression = listOf(
            ResOut,
            Fixed("/layout/"),
            Dynamic(
                parameterId = "fragmentName",
                modifiers = emptyList()
            ),
            Fixed(".xml"),
        ).intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
    }

    "Should recognize {srcOut} variable into separate command" {
        val givenExpressionString = "\${srcOut}/di/\${moduleName}.kt"
        val expectedExpression = listOf(
            SrcOut,
            Fixed("/di/"),
            Dynamic(
                parameterId = "moduleName",
                modifiers = emptyList()
            ),
            Fixed(".kt"),
        ).intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
    }

    "Should recognize {manifestOut} variable into separate command" {
        val givenExpressionString = "\${manifestOut}/AndroidManifest.xml"
        val expectedExpression = listOf(
            RecipeExpressionCommand.ManifestOut,
            Fixed("/AndroidManifest.xml"),
        ).intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
    }

    "Should recognize {rootOut} variable into separate command" {
        val givenExpressionString = "\${rootOut}/build.gradle"
        val expectedExpression = listOf(
            RecipeExpressionCommand.RootOut,
            Fixed("/build.gradle"),
        ).intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
    }

    "Should recognize 'true' string as separate command if it is single word" {
        val givenExpressionString = "true"
        val expectedExpression = listOf(
            ReturnTrue
        ).intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
    }

    "Should recognize 'false' string as separate command" {
        val givenExpressionString = "false"
        val expectedExpression = listOf(
            ReturnFalse
        ).intoExpression()

        givenExpressionString.toRecipeExpression(SECTION_NAME) shouldBe expectedExpression
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

        givenExpressionStringWithTrue.toRecipeExpression(SECTION_NAME) shouldBe expectedExpressionWithTrue
        givenExpressionStringWithFalse.toRecipeExpression(SECTION_NAME) shouldBe expectedExpressionWithFalse
    }

    "Should throw exception when reach unknown modifiers" {
        val givenExpressionString = "\${resOut.unknown()}/layout/\${fragmentName}.xml"

        val ex = shouldThrow<IllegalArgumentException> { givenExpressionString.toRecipeExpression(SECTION_NAME) }

        ex.message shouldStartWith "'$SECTION_NAME' section: Unknown parsing key [key: unknown"
    }

})


