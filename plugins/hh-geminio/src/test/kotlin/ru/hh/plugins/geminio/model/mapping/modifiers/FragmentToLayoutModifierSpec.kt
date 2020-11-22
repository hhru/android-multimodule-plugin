package ru.hh.plugins.geminio.model.mapping.modifiers

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.model.enums.GeminioRecipeExpressionModifier
import ru.hh.plugins.geminio.tests_helpers.GeminioExpressionUtils


class FragmentToLayoutModifierSpec : FreeSpec({

    fun getEvaluatedValue(fragmentName: String): String? {
        return GeminioExpressionUtils.getEvaluatedValue(
            className = fragmentName,
            modifier = GeminioRecipeExpressionModifier.FRAGMENT_TO_LAYOUT
        )
    }


    "Should return normal layout name with 'fragment_' prefix if there is 'Fragment' word" {
        getEvaluatedValue("MyFragment") shouldBe "fragment_my"
        getEvaluatedValue("SomeFragment") shouldBe "fragment_some"
    }

    "Should return null for empty value" {
        getEvaluatedValue("") shouldBe null
    }

    "Should respect single letter in your expression" {
        getEvaluatedValue("A") shouldBe "fragment_a"
        getEvaluatedValue("s") shouldBe "fragment_s"
    }

    "Two or more letters from 'Fragment' word respects as fragment class name suffix (or prefix)" {
        getEvaluatedValue("Fr") shouldBe "fragment_"
        getEvaluatedValue("Fra") shouldBe "fragment_"
        getEvaluatedValue("Frag") shouldBe "fragment_"
        getEvaluatedValue("Fragm") shouldBe "fragment_"
        getEvaluatedValue("Fragme") shouldBe "fragment_"
        getEvaluatedValue("Fragmen") shouldBe "fragment_"
        getEvaluatedValue("Fragment") shouldBe "fragment_"

        getEvaluatedValue("SomeFr") shouldBe "fragment_some"
        getEvaluatedValue("SomeFra") shouldBe "fragment_some"
        getEvaluatedValue("SomeFrag") shouldBe "fragment_some"
        getEvaluatedValue("SomeFragm") shouldBe "fragment_some"
        getEvaluatedValue("SomeFragme") shouldBe "fragment_some"
        getEvaluatedValue("SomeFragmen") shouldBe "fragment_some"
        getEvaluatedValue("SomeFragment") shouldBe "fragment_some"
    }

    "Doesn't matter where you put fragment class prefix" {
        getEvaluatedValue("SomeFragment") shouldBe "fragment_some"
        getEvaluatedValue("FragmentNormal") shouldBe "fragment_normal"
    }


})