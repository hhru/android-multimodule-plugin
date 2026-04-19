package ru.hh.plugins.geminio.sdk.recipe.expressions.modifiers

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier

internal class CamelCaseToUnderlinesModifierSpec : FreeSpec({

    fun getEvaluatedValue(fragmentName: String): String? {
        return GeminioExpressionUtils.getEvaluatedValue(
            className = fragmentName,
            modifier = RecipeExpressionModifier.CAMEL_CASE_TO_UNDERLINES
        )
    }

    "Should be split to several words and joined with '_'" {
        getEvaluatedValue("superCase") shouldBe "super_case"
    }

    "One word will be converted into lower case" {
        getEvaluatedValue("Single") shouldBe "single"
        getEvaluatedValue("one") shouldBe "one"
    }

    "Every letter in upper case should be recognized as separate word" {
        getEvaluatedValue("FAQActivity") shouldBe "f_a_q_activity"
        getEvaluatedValue("BlankFragment") shouldBe "blank_fragment"
    }
})
