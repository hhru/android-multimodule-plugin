package ru.hh.plugins.geminio.sdk.helpers

import com.android.tools.idea.wizard.template.Parameter
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.WizardParameterData
import com.android.tools.idea.wizard.template.template
import com.intellij.openapi.project.Project
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.parsers.parseGeminioRecipeFromYamlFile
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.optional.injectOptionalParams
import ru.hh.plugins.geminio.sdk.template.mapping.required.injectRequiredParams
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.injectWidgets
import java.io.File
import java.lang.reflect.Proxy
import java.nio.file.Files
import java.nio.file.Path

internal data class RecipeFixture(
    val rootDir: Path,
    val recipeFile: Path,
    val recipe: GeminioRecipe,
)

internal data class RecordingRecipeExecutorHandle(
    val executor: RecipeExecutor,
    val openedFiles: MutableList<Path>,
    val savedFiles: MutableMap<Path, String>,
    val createdDirectories: MutableSet<Path>,
)

internal fun createRecipeFixture(
    recipeYaml: String,
    templates: Map<String, String> = emptyMap(),
): RecipeFixture {
    val rootDir = Files.createTempDirectory("geminio-recipe-")
    val recipeFile = rootDir.resolve("recipe.yaml")
    Files.writeString(recipeFile, recipeYaml.trimIndent())

    templates.forEach { (relativePath, content) ->
        val templateFile = rootDir.resolve(relativePath)
        Files.createDirectories(templateFile.parent)
        Files.writeString(templateFile, content.trimIndent())
    }

    return RecipeFixture(
        rootDir = rootDir,
        recipeFile = recipeFile,
        recipe = recipeFile.toString().parseGeminioRecipeFromYamlFile(),
    )
}

internal fun GeminioRecipe.createTemplateParametersMap(): Map<String, AndroidStudioTemplateParameter> {
    val parameters = mutableMapOf<String, AndroidStudioTemplateParameter>()

    template {
        injectRequiredParams(this@createTemplateParametersMap)
        injectOptionalParams(this@createTemplateParametersMap)
        parameters += injectWidgets(this@createTemplateParametersMap)
        recipe = { _ -> }
    }

    val wizardParameterData = WizardParameterData(
        "ru.hh.test",
        false,
        "main",
        parameters.values,
    )
    parameters.values.forEach { parameter ->
        WIZARD_PARAMETER_DATA_SETTER.invoke(parameter, wizardParameterData)
    }

    return parameters
}

internal fun createMockProject(): Project {
    return Proxy.newProxyInstance(
        Project::class.java.classLoader,
        arrayOf(Project::class.java),
    ) { _, method, _ ->
        when (method.name) {
            "getName" -> "test-project"
            "isOpen", "isInitialized" -> false
            "isDefault" -> false
            "toString" -> "MockProject(test-project)"
            "hashCode" -> 0
            "equals" -> false
            else -> defaultValue(method.returnType)
        }
    } as Project
}

internal fun createRecordingRecipeExecutor(): RecordingRecipeExecutorHandle {
    val openedFiles = mutableListOf<Path>()
    val savedFiles = mutableMapOf<Path, String>()
    val createdDirectories = mutableSetOf<Path>()

    val executor = Proxy.newProxyInstance(
        RecipeExecutor::class.java.classLoader,
        arrayOf(RecipeExecutor::class.java),
    ) { _, method, args ->
        when (method.name) {
            "save" -> {
                val text = args[0] as String
                val file = args[1] as File
                file.parentFile?.mkdirs()
                file.writeText(text)
                savedFiles[file.toPath()] = text
                null
            }

            "open" -> {
                val file = args[0] as File
                openedFiles.add(file.toPath())
                null
            }

            "createDirectory" -> {
                val file = args[0] as File
                file.mkdirs()
                createdDirectories.add(file.toPath())
                null
            }

            "copy" -> {
                val from = args[0] as File
                val to = args[1] as File
                to.parentFile?.mkdirs()
                from.copyTo(to, overwrite = true)
                null
            }

            "append" -> {
                val text = args[0] as String
                val file = args[1] as File
                file.parentFile?.mkdirs()
                file.appendText(text)
                savedFiles[file.toPath()] = file.readText()
                null
            }

            "toString" -> "RecordingRecipeExecutor"
            "hashCode" -> 0
            "equals" -> false
            else -> throw UnsupportedOperationException("Method `${method.name}` is not supported in tests")
        }
    } as RecipeExecutor

    return RecordingRecipeExecutorHandle(
        executor = executor,
        openedFiles = openedFiles,
        savedFiles = savedFiles,
        createdDirectories = createdDirectories,
    )
}

private val WIZARD_PARAMETER_DATA_SETTER = Parameter::class.java.getMethod(
    "setWizardParameterData\$intellij_android_wizardTemplate_plugin",
    WizardParameterData::class.java,
)

private fun defaultValue(returnType: Class<*>): Any? {
    return when (returnType) {
        java.lang.Boolean.TYPE -> false
        Integer.TYPE -> 0
        java.lang.Long.TYPE -> 0L
        java.lang.Float.TYPE -> 0f
        java.lang.Double.TYPE -> 0.0
        java.lang.Short.TYPE -> 0.toShort()
        java.lang.Byte.TYPE -> 0.toByte()
        Character.TYPE -> '\u0000'
        else -> null
    }
}
