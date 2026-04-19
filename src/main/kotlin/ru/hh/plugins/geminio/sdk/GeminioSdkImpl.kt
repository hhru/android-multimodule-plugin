package ru.hh.plugins.geminio.sdk

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ru.hh.plugins.geminio.sdk.models.GeminioTemplateData
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.extensions.toIndentString
import ru.hh.plugins.geminio.sdk.recipe.parsers.parseGeminioRecipeFromYamlFile
import ru.hh.plugins.geminio.sdk.template.mapping.toGeminioTemplateData
import ru.hh.plugins.logger.HHLogger
import java.io.File

internal class GeminioSdkImpl : GeminioSdk {

    override fun parseYamlRecipe(recipeFilePath: String): GeminioRecipe {
        HHLogger.d("Search for recipe file: $recipeFilePath")

        check(File(recipeFilePath).exists()) {
            "Recipe file doesn't exists [look into $recipeFilePath]"
        }
        HHLogger.d("Recipe file exists -> need to parse, execute, etc")

        return recipeFilePath.parseGeminioRecipeFromYamlFile().also { recipe ->
            HHLogger.d("geminio recipe to String:\n $recipe")
            HHLogger.d("==========")
            HHLogger.d("geminio recipe:\n ${recipe.toIndentString()}")
        }
    }

    override fun createGeminioTemplateData(
        project: Project,
        geminioRecipe: GeminioRecipe,
        targetDirectory: VirtualFile
    ): GeminioTemplateData {
        return geminioRecipe.toGeminioTemplateData(project, targetDirectory)
    }
}
