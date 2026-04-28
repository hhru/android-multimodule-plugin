package ru.hh.plugins.geminio.sdk.execution

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import ru.hh.plugins.freemarker_wrapper.FreemarkerConfiguration
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathContext
import ru.hh.plugins.geminio.sdk.helpers.createMockProject
import ru.hh.plugins.geminio.sdk.helpers.createRecipeFixture
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

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
        val fileOperations = RecordingGeminioRecipeFileOperations()

        val executionResult = GeminioRecipeRunner(
            fileOperationsFactory = { fileOperations },
        ).run(
            geminioRecipe = fixture.recipe,
            request = createExecutionRequest(
                targetDirectory = targetDirectory,
                parameters = createExecutionParameters(className = "FeatureScreen", includeFactory = true),
                templatesRootDirPath = fixture.rootDir.toString(),
            ),
        )

        fileOperations.createdDirectories.shouldContain(targetDirectory.resolve("generated"))
        Files.isDirectory(targetDirectory.resolve("generated")) shouldBe true
        Files.readString(targetDirectory.resolve("FeatureScreen.kt")) shouldBe "class FeatureScreen"
        Files.readString(
            targetDirectory.resolve("generated/FeatureScreenFactory.kt")
        ) shouldBe "object FeatureScreenFactory"
        executionResult.filesToOpen.shouldContain(targetDirectory.resolve("generated/FeatureScreenFactory.kt").toFile())
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
        val fileOperations = RecordingGeminioRecipeFileOperations()

        val executionResult = GeminioRecipeRunner(
            fileOperationsFactory = { fileOperations },
        ).run(
            geminioRecipe = fixture.recipe,
            request = createExecutionRequest(
                targetDirectory = targetDirectory,
                parameters = createExecutionParameters(className = "FeatureScreen", includeFactory = false),
                templatesRootDirPath = fixture.rootDir.toString(),
            ),
        )

        Files.exists(targetDirectory.resolve("FeatureScreen.kt")) shouldBe true
        Files.exists(targetDirectory.resolve("generated/FeatureScreenFactory.kt")) shouldBe false
        executionResult.filesToOpen.shouldContain(targetDirectory.resolve("FeatureScreen.kt").toFile())
    }
})

private const val EXECUTION_RECIPE = """
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
): Map<String, Any?> {
    return mapOf(
        "className" to className,
        "includeFactory" to includeFactory,
    )
}

private fun createExecutionRequest(
    targetDirectory: Path,
    parameters: Map<String, Any?>,
    templatesRootDirPath: String,
): GeminioRecipeExecutionRequest {
    return GeminioRecipeExecutionRequest(
        project = createMockProject(),
        pathContext = GeminioFormPathContext(
            currentDirOut = targetDirectory.toString(),
        ),
        templateParameters = parameters,
        freemarkerConfiguration = FreemarkerConfiguration(templatesRootDirPath),
    )
}

private class RecordingGeminioRecipeFileOperations : GeminioRecipeFileOperations {

    val createdDirectories = linkedSetOf<Path>()

    override val createdFiles = linkedSetOf<File>()
    override val filesToOpen = linkedSetOf<File>()

    override fun save(source: String, to: File) {
        to.parentFile?.mkdirs()
        to.writeText(source)
        createdFiles += to
    }

    override fun append(source: String, to: File) {
        to.parentFile?.mkdirs()
        to.appendText(source)
        createdFiles += to
    }

    override fun createDirectory(at: File) {
        at.mkdirs()
        createdDirectories.add(at.toPath())
    }

    override fun open(file: File) {
        filesToOpen += file
    }
}
