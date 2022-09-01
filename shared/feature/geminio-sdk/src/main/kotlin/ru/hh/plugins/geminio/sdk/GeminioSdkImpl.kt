package ru.hh.plugins.geminio.sdk

import com.intellij.openapi.project.Project
import ru.hh.plugins.geminio.sdk.models.GeminioTemplateData
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.extensions.toIndentString
import ru.hh.plugins.geminio.sdk.recipe.parsers.parseGeminioRecipeFromYamlFile
import ru.hh.plugins.geminio.sdk.template.mapping.toGeminioTemplateData
import ru.hh.plugins.utils.notifications.Debug
import java.io.File

internal class GeminioSdkImpl : GeminioSdk {

    override fun parseYamlRecipe(recipeFilePath: String): GeminioRecipe {
        Debug.info("Search for recipe file: $recipeFilePath")

        check(File(recipeFilePath).exists()) {
            "Recipe file doesn't exists [look into $recipeFilePath]"
        }
        Debug.info("Recipe file exists -> need to parse, execute, etc")

        return recipeFilePath.parseGeminioRecipeFromYamlFile().also { recipe ->
            Debug.info("geminio recipe to String:\n $recipe")
            Debug.info("==========")
            Debug.info("geminio recipe:\n ${recipe.toIndentString()}")
        }
    }

    override fun createGeminioTemplateData(project: Project, geminioRecipe: GeminioRecipe): GeminioTemplateData {
        return geminioRecipe.toGeminioTemplateData(project)
    }
}
