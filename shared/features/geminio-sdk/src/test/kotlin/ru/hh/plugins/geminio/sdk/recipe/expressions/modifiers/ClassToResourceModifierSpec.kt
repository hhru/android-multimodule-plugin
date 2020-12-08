package ru.hh.plugins.geminio.sdk.recipe.expressions.modifiers

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioRecipeExpressionModifier


class ClassToResourceModifierSpec : FreeSpec({

    fun getEvaluatedValue(fragmentName: String): String? {
        return GeminioExpressionUtils.getEvaluatedValue(
            className = fragmentName,
            modifier = GeminioRecipeExpressionModifier.CLASS_TO_RESOURCE
        )
    }


    "Showcase of using" {
        getEvaluatedValue("BlankFragment") shouldBe "blank"
        getEvaluatedValue("SomeActivity") shouldBe "some"
    }

    "Several words except of common suffixes should be underscored" {
        getEvaluatedValue("PhoneVerificationFragment") shouldBe "phone_verification"
        getEvaluatedValue("BottomSheetDialog") shouldBe "bottom_sheet_dialog"
    }

    "Should remove 'Fragment' prefixes from class name for resource" {
        getEvaluatedValue("Fr") shouldBe null
        getEvaluatedValue("Fra") shouldBe null
        getEvaluatedValue("Frag") shouldBe null
        getEvaluatedValue("Fragm") shouldBe null
        getEvaluatedValue("Fragme") shouldBe null
        getEvaluatedValue("Fragmen") shouldBe null
        getEvaluatedValue("Fragment") shouldBe null
    }

    "Should remove 'Activity' prefixes from class name for resource" {
        getEvaluatedValue("Ac") shouldBe null
        getEvaluatedValue("Act") shouldBe null
        getEvaluatedValue("Acti") shouldBe null
        getEvaluatedValue("Activ") shouldBe null
        getEvaluatedValue("Activi") shouldBe null
        getEvaluatedValue("Activit") shouldBe null
        getEvaluatedValue("Activity") shouldBe null
    }

    "Should remove 'Service' prefixes from class name for resource" {
        getEvaluatedValue("Se") shouldBe null
        getEvaluatedValue("Ser") shouldBe null
        getEvaluatedValue("Serv") shouldBe null
        getEvaluatedValue("Servi") shouldBe null
        getEvaluatedValue("Servic") shouldBe null
        getEvaluatedValue("Service") shouldBe null
    }

    "Should remove 'Provider' prefixes from class name for resource" {
        getEvaluatedValue("Pr") shouldBe null
        getEvaluatedValue("Pro") shouldBe null
        getEvaluatedValue("Prov") shouldBe null
        getEvaluatedValue("Provi") shouldBe null
        getEvaluatedValue("Provid") shouldBe null
        getEvaluatedValue("Provide") shouldBe null
        getEvaluatedValue("Provider") shouldBe null
    }

    "Suffixes will be removed in the following order: 'activity', 'fragment', 'service', 'provider'" {
        getEvaluatedValue("ActivityFragmentServiceProvider") shouldBe "activity_fragment_service"
        getEvaluatedValue("ProviderServiceFragmentActivity") shouldBe null
    }

    "Every letter in upper case will be transformed as separate word" {
        getEvaluatedValue("FAQActivity") shouldBe "f_a_q"
    }

})