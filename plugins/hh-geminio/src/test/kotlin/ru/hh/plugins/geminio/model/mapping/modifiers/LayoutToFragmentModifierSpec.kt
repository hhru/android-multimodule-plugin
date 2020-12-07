package ru.hh.plugins.geminio.model.mapping.modifiers

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.model.enums.GeminioRecipeExpressionModifier
import ru.hh.plugins.geminio.tests_helpers.GeminioExpressionUtils


class LayoutToFragmentModifierSpec : FreeSpec({

    fun getEvaluatedValue(fragmentName: String): String? {
        return GeminioExpressionUtils.getEvaluatedValue(
            className = fragmentName,
            modifier = GeminioRecipeExpressionModifier.LAYOUT_TO_FRAGMENT
        )
    }


    "Should return normal class name with 'Fragment' suffix if there is 'fragment' word in layout name" {
        getEvaluatedValue("fragment_my") shouldBe "MyFragment"
        getEvaluatedValue("fragment_some") shouldBe "SomeFragment"
    }

    "Will return 'MainFragment' for empty value" {
        getEvaluatedValue("") shouldBe "MainFragment"
    }

    "Should respect single letter in your expression" {
        getEvaluatedValue("a") shouldBe "AFragment"
        getEvaluatedValue("s") shouldBe "SFragment"
    }

    "Only full 'Fragment' word respects as Fragment class name suffix (or prefix)" {
        getEvaluatedValue("fr_") shouldBe "FrFragment"
        getEvaluatedValue("fra_") shouldBe "FraFragment"
        getEvaluatedValue("frag_") shouldBe "FragFragment"
        getEvaluatedValue("fragm_") shouldBe "FragmFragment"
        getEvaluatedValue("fragme_") shouldBe "FragmeFragment"
        getEvaluatedValue("fragmen_") shouldBe "FragmenFragment"

        getEvaluatedValue("fr_some") shouldBe "FrSomeFragment"
        getEvaluatedValue("fra_some") shouldBe "FraSomeFragment"
        getEvaluatedValue("frag_some") shouldBe "FragSomeFragment"
        getEvaluatedValue("fragm_some") shouldBe "FragmSomeFragment"
        getEvaluatedValue("fragme_some") shouldBe "FragmeSomeFragment"
        getEvaluatedValue("fragmen_some") shouldBe "FragmenSomeFragment"
        getEvaluatedValue("fragment_some") shouldBe "SomeFragment"
    }

    "Only prefix 'fragment_' will be transformed into default value" {
        getEvaluatedValue("fragment_") shouldBe "MainFragment"
    }

    "It really matters where you put Fragment prefix" {
        getEvaluatedValue("some_fragment") shouldBe "SomeFragmentFragment"
        getEvaluatedValue("fragment_normal") shouldBe "NormalFragment"
    }

    "Extra '_' will be skipped" {
        getEvaluatedValue("fragment__normal") shouldBe "NormalFragment"
        getEvaluatedValue("some__fragment__good") shouldBe "SomeFragmentGoodFragment"
    }

})