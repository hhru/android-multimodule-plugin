package ru.hh.plugins.geminio.sdk.execution

import com.android.tools.idea.wizard.template.booleanParameter
import com.android.tools.idea.wizard.template.stringParameter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import ru.hh.plugins.freemarker_wrapper.FreemarkerConfiguration
import ru.hh.plugins.geminio.sdk.helpers.GeminioExpressionUtils.createModuleTemplateData
import ru.hh.plugins.geminio.sdk.helpers.RawPathVirtualFile
import ru.hh.plugins.geminio.sdk.helpers.createMockProject
import ru.hh.plugins.geminio.sdk.helpers.createRecipeFixture
import ru.hh.plugins.geminio.sdk.helpers.createRecordingRecipeExecutor
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.executors.executeGeminioRecipe
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData
import java.nio.file.Files

/**
 * Фиксирует исполнение mkDirs, instantiate, instantiateAndOpen, predicate и elseCommands.
 */
internal class GeminioRecipeExecutionCharacterizationSpec : FreeSpec({

    "should execute instantiate, instantiateAndOpen and mkDirs commands" {
        val fixture = createRecipeFixture(
            recipeYaml = EXECUTION_RECIPE,
            templates = mapOf(
                "templates/Main.kt.ftl" to "class ${'$'}{className}",
                "templates/Factory.kt.ftl" to "object ${'$'}{className}Factory",
            ),
        )
        val targetDirectory = Files.createTempDirectory("geminio-target-")
        val parameters = createExecutionParameters(className = "FeatureScreen", includeFactory = true)
        val recipeExecutorHandle = createRecordingRecipeExecutor()

        recipeExecutorHandle.executor.executeGeminioRecipe(
            targetDirectory = RawPathVirtualFile(targetDirectory.toString()),
            geminioRecipe = fixture.recipe,
            executorData = createExecutorData(parameters, fixture.rootDir.toString()),
        )

        recipeExecutorHandle.createdDirectories.shouldContain(targetDirectory.resolve("generated"))
        Files.isDirectory(targetDirectory.resolve("generated")) shouldBe true
        Files.readString(targetDirectory.resolve("FeatureScreen.kt")) shouldBe "class FeatureScreen"
        Files.readString(targetDirectory.resolve("generated/FeatureScreenFactory.kt")) shouldBe "object FeatureScreenFactory"
        recipeExecutorHandle.openedFiles.shouldContain(targetDirectory.resolve("generated/FeatureScreenFactory.kt"))
    }

    "should execute elseCommands when predicate is false" {
        val fixture = createRecipeFixture(
            recipeYaml = EXECUTION_RECIPE,
            templates = mapOf(
                "templates/Main.kt.ftl" to "class ${'$'}{className}",
                "templates/Factory.kt.ftl" to "object ${'$'}{className}Factory",
            ),
        )
        val targetDirectory = Files.createTempDirectory("geminio-target-")
        val parameters = createExecutionParameters(className = "FeatureScreen", includeFactory = false)
        val recipeExecutorHandle = createRecordingRecipeExecutor()

        recipeExecutorHandle.executor.executeGeminioRecipe(
            targetDirectory = RawPathVirtualFile(targetDirectory.toString()),
            geminioRecipe = fixture.recipe,
            executorData = createExecutorData(parameters, fixture.rootDir.toString()),
        )

        Files.exists(targetDirectory.resolve("FeatureScreen.kt")) shouldBe true
        Files.exists(targetDirectory.resolve("generated/FeatureScreenFactory.kt")) shouldBe false
        recipeExecutorHandle.openedFiles.shouldContain(targetDirectory.resolve("FeatureScreen.kt"))
    }
})

private val EXECUTION_RECIPE = """
requiredParams:
  name: Geminio execution test
  description: Covers command execution behavior

widgets:
  - stringParameter:
      id: className
      name: Class name
      default: FeatureScreen

  - booleanParameter:
      id: includeFactory
      name: Include factory
      default: true

recipe:
  - mkDirs:
      - ${'$'}{currentDirOut}:
          - generated
  - instantiate:
      from: templates/Main.kt.ftl
      to: ${'$'}{currentDirOut}/${'$'}{className}.kt
  - predicate:
      validIf: ${'$'}{includeFactory}
      commands:
        - instantiateAndOpen:
            from: templates/Factory.kt.ftl
            to: ${'$'}{currentDirOut}/generated/${'$'}{className}Factory.kt
      elseCommands:
        - open:
            file: ${'$'}{currentDirOut}/${'$'}{className}.kt
"""

private fun createExecutionParameters(
    className: String,
    includeFactory: Boolean,
): Map<String, AndroidStudioTemplateParameter> {
    val classNameParameter = stringParameter {
        name = "Class name"
        default = className
    }.apply {
        value = className
    }
    val includeFactoryParameter = booleanParameter {
        name = "Include factory"
        default = includeFactory
    }.apply {
        value = includeFactory
    }

    return mapOf(
        "className" to classNameParameter,
        "includeFactory" to includeFactoryParameter,
    )
}

private fun createExecutorData(
    parameters: Map<String, AndroidStudioTemplateParameter>,
    templatesRootDirPath: String,
): GeminioRecipeExecutorData {
    return GeminioRecipeExecutorData(
        project = createMockProject(),
        isDryRun = false,
        moduleTemplateData = createModuleTemplateData(),
        existingParametersMap = parameters,
        resolvedParamsMap = parameters.asIterable().associate { entry ->
            entry.key to entry.value.value
        },
        freemarkerConfiguration = FreemarkerConfiguration(templatesRootDirPath),
    )
}
