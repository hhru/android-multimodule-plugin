package ru.hh.plugins.geminio.sdk.recipe.expressions.modifiers

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioRecipeExpressionModifier
import ru.hh.plugins.geminio.tests_helpers.GeminioExpressionUtils


class UnderlinesToCamelCaseModifierSpec : FreeSpec({

    fun getEvaluatedValue(fragmentName: String): String? {
        return GeminioExpressionUtils.getEvaluatedValue(
            className = fragmentName,
            modifier = GeminioRecipeExpressionModifier.UNDERLINES_TO_CAMEL_CASE
        )
    }


    "Showcase of using" {
        getEvaluatedValue("fragment_blank") shouldBe "FragmentBlank"
        getEvaluatedValue("blank_fragment") shouldBe "BlankFragment"
    }

    "Single work will be capitalized" {
        getEvaluatedValue("single") shouldBe "Single"
        getEvaluatedValue("Next") shouldBe "Next"
    }

    "Several '_' will be translated as '_'" {
        getEvaluatedValue("fragment__blank") shouldBe "FragmentBlank"
    }

    "Should return null if there is no other symbols except '_'" {
        getEvaluatedValue("_") shouldBe null
        getEvaluatedValue("__") shouldBe null
    }

    "Should return null for empty string" {
        getEvaluatedValue("") shouldBe null
    }

    "Skip extra '_' around main content" {
        getEvaluatedValue("_some_word") shouldBe "SomeWord"
        getEvaluatedValue("some_word_") shouldBe "SomeWord"
        getEvaluatedValue("_some_word_") shouldBe "SomeWord"
    }

})