package ru.hh.plugins.geminio.sdk.helpers

import com.intellij.openapi.project.Project
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.parsers.parseGeminioRecipeFromYamlFile
import java.lang.reflect.Proxy
import java.nio.file.Files
import java.nio.file.Path

internal data class RecipeFixture(
    val rootDir: Path,
    val recipeFile: Path,
    val recipe: GeminioRecipe,
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
