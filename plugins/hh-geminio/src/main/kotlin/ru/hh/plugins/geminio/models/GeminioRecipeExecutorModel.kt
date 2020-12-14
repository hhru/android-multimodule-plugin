package ru.hh.plugins.geminio.models

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor


data class GeminioRecipeExecutorModel(
    val recipeExecutor: RecipeExecutor,
    val moduleTemplateData: ModuleTemplateData
)