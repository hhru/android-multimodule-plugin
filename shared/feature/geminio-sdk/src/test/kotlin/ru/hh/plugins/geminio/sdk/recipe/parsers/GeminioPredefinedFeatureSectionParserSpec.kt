package ru.hh.plugins.geminio.sdk.recipe.parsers

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import org.yaml.snakeyaml.Yaml
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.recipe.parsers.predefined.toPredefinedFeaturesSection

internal class GeminioPredefinedFeatureSectionParserSpec : FreeSpec({
    "Should support predefined feature section without params" {
        val predefineSection = """
            predefinedFeatures:
                - enableModuleCreationParams
        """.trimIndent()
        val parsed: Map<String, Any> = Yaml().load(predefineSection)
        val expected = PredefinedFeaturesSection(
            mapOf(
                PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS to
                        PredefinedFeatureParameter.ModuleCreationParameter()
            )
        )

        parsed.toPredefinedFeaturesSection() shouldBe expected
    }

    "Should support predefined feature section with defaultPackageNamePrefix" {
        val testPackageNamePrefix = "ru.hh.test"
        val predefineSection = """
            predefinedFeatures:
                - enableModuleCreationParams:
                    defaultPackageNamePrefix: $testPackageNamePrefix
        """.trimIndent()
        val parsed: Map<String, Any> = Yaml().load(predefineSection)
        val expected = PredefinedFeaturesSection(
            mapOf(
                PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS to
                        PredefinedFeatureParameter.ModuleCreationParameter(testPackageNamePrefix)
            )
        )

        parsed.toPredefinedFeaturesSection() shouldBe expected
    }

    "Should ignore if defaultPackageNamePrefix not exist" {
        val predefineSection = """
            predefinedFeatures:
                - enableModuleCreationParams:
                    someDifferentParam: other
        """.trimIndent()
        val parsed: Map<String, Any> = Yaml().load(predefineSection)
        val expected = PredefinedFeaturesSection(
            mapOf(
                PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS to
                        PredefinedFeatureParameter.ModuleCreationParameter()
            )
        )

        parsed.toPredefinedFeaturesSection() shouldBe expected
    }
})