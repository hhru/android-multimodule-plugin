@file:Suppress("detekt.Indentation")

package ru.hh.plugins.geminio.sdk.form

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_MODULE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_SOURCE_SET_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.GLOBALS_SHOW_HIDDEN_VALUES_ID
import ru.hh.plugins.geminio.sdk.helpers.createRecipeFixture

/**
 * Проверяет работу нового runtime-слоя UI-формы после парсинга geminio-рецепта.
 */
internal class GeminioPureFormRuntimeSpec : FreeSpec({

    "should build stable form structure for widgets and globals" {
        val fixture = createRecipeFixture(RECIPE_WITH_WIDGETS_AND_GLOBALS)
        val form = fixture.recipe.toGeminioForm()

        form.fields.map { it.id }.shouldContainExactly(
            "className",
            "generateFactory",
            "factoryName",
            GLOBALS_SHOW_HIDDEN_VALUES_ID,
            "generatedClass",
            "shouldRenderFactory",
        )

        form.requireField("className").origin shouldBe GeminioFormFieldOrigin.WIDGET
        form.requireField(GLOBALS_SHOW_HIDDEN_VALUES_ID).origin shouldBe GeminioFormFieldOrigin.INTERNAL
        form.requireField("generatedClass").origin shouldBe GeminioFormFieldOrigin.GLOBAL
    }

    "should evaluate widgets visibility availability and suggestions" {
        val fixture = createRecipeFixture(RECIPE_WITH_WIDGETS_AND_GLOBALS)
        val session = GeminioFormSession(fixture.recipe.toGeminioForm())

        session.stringValue("className") shouldBe "BlankScreen"
        session.booleanValue("generateFactory") shouldBe false

        session.fieldState("factoryName").visible shouldBe false
        session.fieldState("factoryName").enabled shouldBe false
        session.suggestedStringValue("factoryName") shouldBe "BlankScreenFactory"

        session.setBooleanValue("generateFactory", true)
        session.setStringValue("className", "FeedScreen")

        session.fieldState("factoryName").visible shouldBe true
        session.fieldState("factoryName").enabled shouldBe true
        session.applySuggestion("factoryName") shouldBe "FeedScreenFactory"
        session.stringValue("factoryName") shouldBe "FeedScreenFactory"
    }

    "should keep globals hidden until toggle is enabled while preserving computed values" {
        val fixture = createRecipeFixture(RECIPE_WITH_WIDGETS_AND_GLOBALS)
        val session = GeminioFormSession(fixture.recipe.toGeminioForm())

        session.booleanValue(GLOBALS_SHOW_HIDDEN_VALUES_ID) shouldBe false
        session.stringValue("generatedClass") shouldBe "BlankScreenGenerated"
        session.booleanValue("shouldRenderFactory") shouldBe false

        session.fieldState("generatedClass").visible shouldBe false
        session.fieldState("shouldRenderFactory").visible shouldBe false
        session.suggestedStringValue("generatedClass") shouldBe "BlankScreenGenerated"

        session.setBooleanValue(GLOBALS_SHOW_HIDDEN_VALUES_ID, true)
        session.setStringValue("className", "FeedScreen")
        session.setBooleanValue("generateFactory", true)

        session.fieldState("generatedClass").visible shouldBe true
        session.fieldState("shouldRenderFactory").visible shouldBe true
        session.suggestedStringValue("generatedClass") shouldBe "FeedScreenGenerated"
        session.applySuggestion("generatedClass") shouldBe "FeedScreenGenerated"
    }

    "should expose predefined module parameters with derived suggestions" {
        val fixture = createRecipeFixture(RECIPE_WITH_PREDEFINED_MODULE_PARAMS)
        val form = fixture.recipe.toGeminioForm()
        val session = GeminioFormSession(form)

        form.fields.map { it.id }.shouldContainExactly(
            FEATURE_MODULE_NAME_PARAMETER_ID,
            FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID,
            FEATURE_PACKAGE_NAME_PARAMETER_ID,
            FEATURE_SOURCE_SET_PARAMETER_ID,
            FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID,
        )

        session.stringValue(FEATURE_MODULE_NAME_PARAMETER_ID) shouldBe "mymodule"
        session.stringValue(FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID) shouldBe "MyModule"
        session.stringValue(FEATURE_PACKAGE_NAME_PARAMETER_ID) shouldBe "ru.hh.feature.mymodule"
        session.stringValue(FEATURE_SOURCE_SET_PARAMETER_ID) shouldBe "testFixtures"
        session.stringValue(FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID) shouldBe "kotlin"

        session.setStringValue(FEATURE_MODULE_NAME_PARAMETER_ID, "feature-auth")

        session.suggestedStringValue(FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID) shouldBe "FeatureAuth"
        session.suggestedStringValue(FEATURE_PACKAGE_NAME_PARAMETER_ID) shouldBe "ru.hh.feature.feature_auth"
    }
})

private const val RECIPE_WITH_WIDGETS_AND_GLOBALS = """
requiredParams:
  name: Geminio pure form test
  description: Covers widgets and globals behavior

widgets:
  - stringParameter:
      id: className
      name: Class name
      help: Name of generated class
      constraints:
        - class
        - nonempty
      default: BlankScreen

  - booleanParameter:
      id: generateFactory
      name: Generate factory
      default: false

  - stringParameter:
      id: factoryName
      name: Factory name
      default: BlankScreenFactory
      suggest: ${'$'}{className}Factory
      visibility: ${'$'}{generateFactory}
      availability: ${'$'}{generateFactory}

globals:
  - stringParameter:
      id: generatedClass
      value: ${'$'}{className}Generated

  - booleanParameter:
      id: shouldRenderFactory
      value: ${'$'}{generateFactory}

recipe: []
"""

private const val RECIPE_WITH_PREDEFINED_MODULE_PARAMS = """
requiredParams:
  name: Geminio module test
  description: Covers predefined module params

predefinedFeatures:
  - enableModuleCreationParams:
      defaultPackageNamePrefix: ru.hh.feature
      defaultSourceSetName: testFixtures
      defaultSourceCodeFolderName: kotlin

widgets: []
recipe: []
"""
