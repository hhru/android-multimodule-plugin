package ru.hh.plugins.geminio.sdk.recipe.expressions.modifiers

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier

internal class ActivityToLayoutModifierSpec : FreeSpec({

    fun getEvaluatedValue(activityName: String): String? {
        return GeminioExpressionUtils.getEvaluatedValue(
            className = activityName,
            modifier = RecipeExpressionModifier.ACTIVITY_TO_LAYOUT
        )
    }

    "Should return normal layout name with 'activity_' prefix if there is 'Activity' word" {
        getEvaluatedValue("MyActivity") shouldBe "activity_my"
        getEvaluatedValue("SomeActivity") shouldBe "activity_some"
    }

    "Should return null for empty value" {
        getEvaluatedValue("") shouldBe null
    }

    "Should respect single letter in your expression" {
        getEvaluatedValue("A") shouldBe "activity_a"
        getEvaluatedValue("s") shouldBe "activity_s"
    }

    "Two or more letters from 'Activity' word respects as activity class name suffix (or prefix)" {
        getEvaluatedValue("Ac") shouldBe "activity_"
        getEvaluatedValue("Act") shouldBe "activity_"
        getEvaluatedValue("Acti") shouldBe "activity_"
        getEvaluatedValue("Activ") shouldBe "activity_"
        getEvaluatedValue("Activi") shouldBe "activity_"
        getEvaluatedValue("Activit") shouldBe "activity_"
        getEvaluatedValue("Activity") shouldBe "activity_"

        getEvaluatedValue("SomeAc") shouldBe "activity_some"
        getEvaluatedValue("SomeAct") shouldBe "activity_some"
        getEvaluatedValue("SomeActi") shouldBe "activity_some"
        getEvaluatedValue("SomeActiv") shouldBe "activity_some"
        getEvaluatedValue("SomeActivi") shouldBe "activity_some"
        getEvaluatedValue("SomeActivit") shouldBe "activity_some"
        getEvaluatedValue("SomeActivity") shouldBe "activity_some"
    }

    "Doesn't matter where you put activity class prefix" {
        getEvaluatedValue("SomeActivity") shouldBe "activity_some"
        getEvaluatedValue("ActivityNormal") shouldBe "activity_normal"
    }
})
