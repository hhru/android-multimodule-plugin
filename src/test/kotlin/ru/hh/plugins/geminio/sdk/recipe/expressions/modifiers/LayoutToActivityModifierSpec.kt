package ru.hh.plugins.geminio.sdk.recipe.expressions.modifiers

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier

internal class LayoutToActivityModifierSpec : FreeSpec({

    fun getEvaluatedValue(activityName: String): String? {
        return GeminioExpressionUtils.getEvaluatedValue(
            className = activityName,
            modifier = RecipeExpressionModifier.LAYOUT_TO_ACTIVITY
        )
    }

    "Should return normal class name with 'Activity' suffix if there is 'activity' word in layout name" {
        getEvaluatedValue("activity_my") shouldBe "MyActivity"
        getEvaluatedValue("activity_some") shouldBe "SomeActivity"
    }

    "Will return 'MainActivity' for empty value" {
        getEvaluatedValue("") shouldBe "MainActivity"
    }

    "Should respect single letter in your expression" {
        getEvaluatedValue("a") shouldBe "AActivity"
        getEvaluatedValue("s") shouldBe "SActivity"
    }

    "Only full 'activity' word respects as activity class name suffix (or prefix)" {
        getEvaluatedValue("ac_") shouldBe "AcActivity"
        getEvaluatedValue("act_") shouldBe "ActActivity"
        getEvaluatedValue("acti_") shouldBe "ActiActivity"
        getEvaluatedValue("activ_") shouldBe "ActivActivity"
        getEvaluatedValue("activi_") shouldBe "ActiviActivity"
        getEvaluatedValue("activit_") shouldBe "ActivitActivity"

        getEvaluatedValue("ac_some") shouldBe "AcSomeActivity"
        getEvaluatedValue("act_some") shouldBe "ActSomeActivity"
        getEvaluatedValue("acti_some") shouldBe "ActiSomeActivity"
        getEvaluatedValue("activ_some") shouldBe "ActivSomeActivity"
        getEvaluatedValue("activi_some") shouldBe "ActiviSomeActivity"
        getEvaluatedValue("activit_some") shouldBe "ActivitSomeActivity"
        getEvaluatedValue("activity_some") shouldBe "SomeActivity"
    }

    "Only prefix 'activity_' will be transformed into default value" {
        getEvaluatedValue("activity_") shouldBe "MainActivity"
    }

    "It really matters where you put activity prefix" {
        getEvaluatedValue("some_activity") shouldBe "SomeActivityActivity"
        getEvaluatedValue("activity_normal") shouldBe "NormalActivity"
    }

    "Extra '_' will be skipped" {
        getEvaluatedValue("activity__normal") shouldBe "NormalActivity"
        getEvaluatedValue("some__activity__good") shouldBe "SomeActivityGoodActivity"
    }
})
