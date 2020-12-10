package ru.hh.plugins.geminio.sdk

import com.intellij.openapi.project.Project
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate


/**
 * SDK with Geminio functionality.
 */
interface GeminioSdk {

    fun parseYamlRecipe(recipeFilePath: String): GeminioRecipe

    fun createAndroidStudioTemplate(project: Project, geminioRecipe: GeminioRecipe): AndroidStudioTemplate

}