package ru.hh.plugins.geminio.sdk.template.mapping

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.template
import com.intellij.openapi.project.Project
import ru.hh.plugins.freemarker_wrapper.FreemarkerConfiguration
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate
import ru.hh.plugins.geminio.sdk.template.executors.executeGeminioRecipe
import ru.hh.plugins.geminio.sdk.template.mapping.optional.injectOptionalParams
import ru.hh.plugins.geminio.sdk.template.mapping.required.injectRequiredParams
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.injectWidgets
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate].
 */
internal fun GeminioRecipe.toAndroidStudioTemplate(project: Project): AndroidStudioTemplate {
    val geminioRecipe = this

    return template {
        injectRequiredParams(geminioRecipe)
        injectOptionalParams(geminioRecipe)

        val existingParametersMap = injectWidgets(geminioRecipe)

        var isDryRun = true
        recipe = { templateData ->
            val moduleTemplateData = templateData as ModuleTemplateData
            executeGeminioRecipe(
                geminioRecipe = geminioRecipe,
                executorData = GeminioRecipeExecutorData(
                    project = project,
                    isDryRun = isDryRun,
                    moduleTemplateData = moduleTemplateData,
                    existingParametersMap = existingParametersMap,
                    resolvedParamsMap = existingParametersMap.asIterable().associate { entry ->
                        entry.key to entry.value.value
                    }.plus(
                        mapOf(
                            HardcodedParams.PACKAGE_NAME to moduleTemplateData.packageName,
                            HardcodedParams.APPLICATION_PACKAGE to moduleTemplateData.projectTemplateData.applicationPackage
                        )
                    ),
                    freemarkerConfiguration = FreemarkerConfiguration(geminioRecipe.freemarkerTemplatesRootDirPath)
                )
            )
            isDryRun = false
        }
    }
}


private object HardcodedParams {
    /**
     * Package name from SELECTED file.
     */
    const val PACKAGE_NAME = "packageName"

    /**
     * Package name from current gradle module.
     */
    const val APPLICATION_PACKAGE = "applicationPackage"
}