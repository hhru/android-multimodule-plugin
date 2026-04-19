package ru.hh.plugins.geminio.sdk.form

import com.android.tools.idea.wizard.template.BooleanParameter
import com.android.tools.idea.wizard.template.StringParameter
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
import ru.hh.plugins.geminio.sdk.helpers.createTemplateParametersMap

/**
 * Фиксирует текущее поведение формы: widgets, globals и predefined module params.
 */
internal class GeminioFormCharacterizationSpec : FreeSpec({

    "should preserve widgets defaults, constraints and dynamic state" {
        val fixture = createRecipeFixture(RECIPE_WITH_WIDGETS_AND_GLOBALS)
        val parameters = fixture.recipe.createTemplateParametersMap()

        val className = parameters["className"] as StringParameter
        val generateFactory = parameters["generateFactory"] as BooleanParameter
        val factoryName = parameters["factoryName"] as StringParameter

        className.defaultValue shouldBe "BlankScreen"
        className.constraints.map { it.name }.shouldContainExactly("CLASS", "NONEMPTY")

        generateFactory.defaultValue shouldBe false

        factoryName.defaultValue shouldBe "BlankScreenFactory"
        factoryName.visible shouldBe false
        factoryName.enabled shouldBe false

        generateFactory.value = true
        className.value = "FeedScreen"

        factoryName.visible shouldBe true
        factoryName.enabled shouldBe true
        factoryName.suggest() shouldBe "FeedScreenFactory"
        factoryName.value shouldBe "FeedScreenFactory"
    }

    "should keep globals hidden until special checkbox is enabled" {
        val fixture = createRecipeFixture(RECIPE_WITH_WIDGETS_AND_GLOBALS)
        val parameters = fixture.recipe.createTemplateParametersMap()

        val toggleGlobals = parameters[GLOBALS_SHOW_HIDDEN_VALUES_ID] as BooleanParameter
        val className = parameters["className"] as StringParameter
        val generatedClass = parameters["generatedClass"] as StringParameter
        val shouldRenderFactory = parameters["shouldRenderFactory"] as BooleanParameter

        toggleGlobals.defaultValue shouldBe false
        toggleGlobals.visible shouldBe true
        generatedClass.visible shouldBe false
        shouldRenderFactory.visible shouldBe false

        generatedClass.suggest() shouldBe "BlankScreenGenerated"

        toggleGlobals.value = true
        className.value = "FeedScreen"

        generatedClass.visible shouldBe true
        shouldRenderFactory.visible shouldBe true
        generatedClass.suggest() shouldBe "FeedScreenGenerated"
    }

    "should expose predefined module parameters with derived suggestions" {
        val fixture = createRecipeFixture(RECIPE_WITH_PREDEFINED_MODULE_PARAMS)
        val parameters = fixture.recipe.createTemplateParametersMap()

        val moduleName = parameters[FEATURE_MODULE_NAME_PARAMETER_ID] as StringParameter
        val formattedModuleName = parameters[FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID] as StringParameter
        val packageName = parameters[FEATURE_PACKAGE_NAME_PARAMETER_ID] as StringParameter
        val sourceSet = parameters[FEATURE_SOURCE_SET_PARAMETER_ID] as StringParameter
        val sourceCodeFolder = parameters[FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID] as StringParameter

        moduleName.defaultValue shouldBe "mymodule"
        formattedModuleName.defaultValue shouldBe "MyModule"
        packageName.defaultValue shouldBe "ru.hh.feature.mymodule"
        sourceSet.defaultValue shouldBe "testFixtures"
        sourceCodeFolder.defaultValue shouldBe "kotlin"

        moduleName.value = "feature-auth"

        formattedModuleName.suggest() shouldBe "FeatureAuth"
        packageName.suggest() shouldBe "ru.hh.feature.feature_auth"
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

private val RECIPE_WITH_PREDEFINED_MODULE_PARAMS = """
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
