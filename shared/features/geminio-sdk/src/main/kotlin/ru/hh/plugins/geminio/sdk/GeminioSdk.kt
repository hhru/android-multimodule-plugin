package ru.hh.plugins.geminio.sdk

import com.android.tools.idea.npw.template.ConfigureTemplateParametersStep


/**
 * SDK with Geminio functionality.
 */
interface GeminioSdk {

    fun parseGeminioRecipe(
        recipeFilePath: String
    ): ConfigureTemplateParametersStep

}