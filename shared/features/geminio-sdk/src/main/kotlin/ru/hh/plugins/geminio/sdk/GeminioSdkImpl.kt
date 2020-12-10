package ru.hh.plugins.geminio.sdk

import com.intellij.openapi.project.Project
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.extensions.toIndentString
import ru.hh.plugins.geminio.sdk.recipe.parsers.parseGeminioRecipeFromYamlFile
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate
import ru.hh.plugins.geminio.sdk.template.mapping.toAndroidStudioTemplate
import java.io.File


internal class GeminioSdkImpl : GeminioSdk {

    override fun parseYamlRecipe(recipeFilePath: String): GeminioRecipe {
        println("Search for recipe file: $recipeFilePath")

        check(File(recipeFilePath).exists()) {
            "Recipe file doesn't exists [look into $recipeFilePath]"
        }
        println("Recipe file exists -> need to parse, execute, etc")

        return recipeFilePath.parseGeminioRecipeFromYamlFile().also { recipe ->
            println("geminio recipe to String:\n $recipe")
            println("==========")
            println("geminio recipe:\n ${recipe.toIndentString()}")
        }
    }

    override fun createAndroidStudioTemplate(project: Project, geminioRecipe: GeminioRecipe): AndroidStudioTemplate {
        return geminioRecipe.toAndroidStudioTemplate(project)
    }

}