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
 * Фиксирует текущее поведение формы: widgets, globals и predefined module params.
 */
internal class GeminioFormCharacterizationSpec : FreeSpec({

    "should preserve widgets defaults, constraints and dynamic state" {
        val fixture = createRecipeFixture(RECIPE_WITH_WIDGETS_AND_GLOBALS)
        val form = fixture.recipe.toGeminioForm()
        val session = GeminioFormSession(form)
        val className = form.requireField("className") as GeminioFormField.StringField
        val generateFactory = form.requireField("generateFactory") as GeminioFormField.BooleanField
        val factoryName = form.requireField("factoryName") as GeminioFormField.StringField

        className.defaultValue shouldBe "BlankScreen"
        className.constraints.map { it.name }.shouldContainExactly("CLASS", "NONEMPTY")

        generateFactory.defaultValue shouldBe false

        factoryName.defaultValue shouldBe "BlankScreenFactory"
        session.fieldState("factoryName").visible shouldBe false
        session.fieldState("factoryName").enabled shouldBe false

        session.setBooleanValue("generateFactory", true)
        session.setStringValue("className", "FeedScreen")

        session.fieldState("factoryName").visible shouldBe true
        session.fieldState("factoryName").enabled shouldBe true
        session.applySuggestion("factoryName") shouldBe "FeedScreenFactory"
        session.stringValue("factoryName") shouldBe "FeedScreenFactory"
    }

    "should keep globals hidden until special checkbox is enabled" {
        val fixture = createRecipeFixture(RECIPE_WITH_WIDGETS_AND_GLOBALS)
        val session = GeminioFormSession(fixture.recipe.toGeminioForm())

        session.booleanValue(GLOBALS_SHOW_HIDDEN_VALUES_ID) shouldBe false
        session.fieldState(GLOBALS_SHOW_HIDDEN_VALUES_ID).visible shouldBe true
        session.fieldState("generatedClass").visible shouldBe false
        session.fieldState("shouldRenderFactory").visible shouldBe false

        session.suggestedStringValue("generatedClass") shouldBe "BlankScreenGenerated"

        session.setBooleanValue(GLOBALS_SHOW_HIDDEN_VALUES_ID, true)
        session.setStringValue("className", "FeedScreen")

        session.fieldState("generatedClass").visible shouldBe true
        session.fieldState("shouldRenderFactory").visible shouldBe true
        session.suggestedStringValue("generatedClass") shouldBe "FeedScreenGenerated"
    }

    "should expose predefined module parameters with derived suggestions" {
        val fixture = createRecipeFixture(RECIPE_WITH_PREDEFINED_MODULE_PARAMS)
        val form = fixture.recipe.toGeminioForm()
        val session = GeminioFormSession(form)
        val moduleName = form.requireField(FEATURE_MODULE_NAME_PARAMETER_ID) as GeminioFormField.StringField
        val formattedModuleName = form.requireField(
            FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID
        ) as GeminioFormField.StringField
        val packageName = form.requireField(FEATURE_PACKAGE_NAME_PARAMETER_ID) as GeminioFormField.StringField
        val sourceSet = form.requireField(FEATURE_SOURCE_SET_PARAMETER_ID) as GeminioFormField.StringField
        val sourceCodeFolder = form.requireField(
            FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID
        ) as GeminioFormField.StringField

        moduleName.defaultValue shouldBe "mymodule"
        formattedModuleName.defaultValue shouldBe "MyModule"
        packageName.defaultValue shouldBe "ru.hh.feature.mymodule"
        sourceSet.defaultValue shouldBe "testFixtures"
        sourceCodeFolder.defaultValue shouldBe "kotlin"

        session.setStringValue(FEATURE_MODULE_NAME_PARAMETER_ID, "feature-auth")

        session.suggestedStringValue(FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID) shouldBe "FeatureAuth"
        session.suggestedStringValue(FEATURE_PACKAGE_NAME_PARAMETER_ID) shouldBe "ru.hh.feature.feature_auth"
    }
})

private const val RECIPE_WITH_WIDGETS_AND_GLOBALS = """
requiredParams:
  name: Geminio form test
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
