package ru.hh.plugins.geminio.sdk.template

import com.android.tools.idea.wizard.template.BooleanParameter
import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.CheckBoxWidget
import com.android.tools.idea.wizard.template.EnumParameter
import com.android.tools.idea.wizard.template.EnumWidget
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.StringParameter
import com.android.tools.idea.wizard.template.TemplateBuilder
import com.android.tools.idea.wizard.template.TextFieldWidget
import com.android.tools.idea.wizard.template.template
import com.intellij.openapi.project.Project
import ru.hh.plugins.freemarker_wrapper.FreemarkerConfiguration
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.toAndroidStudioTemplateCategory
import ru.hh.plugins.geminio.sdk.template.mapping.toAndroidStudioTemplateConstraint
import ru.hh.plugins.geminio.sdk.template.mapping.toAndroidStudioTemplateFormFactor
import ru.hh.plugins.geminio.sdk.template.mapping.toAndroidStudioTemplateIdParameterPair
import ru.hh.plugins.geminio.sdk.template.mapping.toAndroidStudioTemplateWizardUiContext
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeExecutorData


/**
 * Build Android Studio [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplate]
 * from [ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe].
 */
fun geminioTemplate(project: Project, geminioRecipe: GeminioRecipe): AndroidStudioTemplate = template {
    injectRequiredParams(geminioRecipe)
    injectOptionalParams(geminioRecipe)

    val existingParametersMap = injectWidgets(geminioRecipe)

    var isDryRun = true
    recipe = { templateData ->
        val moduleTemplateData = templateData as ModuleTemplateData
        geminioRecipe(
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
                        GeminioSdkConstants.HardcodedParams.PACKAGE_NAME to moduleTemplateData.packageName,
                        GeminioSdkConstants.HardcodedParams.APPLICATION_PACKAGE to moduleTemplateData.projectTemplateData.applicationPackage
                    )
                ),
                freemarkerConfiguration = FreemarkerConfiguration(geminioRecipe.freemarkerTemplatesRootDirPath)
            )
        )
        isDryRun = false
    }
}


private fun TemplateBuilder.injectRequiredParams(geminioRecipe: GeminioRecipe) {
    with(geminioRecipe) {
        name = requiredParams.name
        description = requiredParams.description
    }
}

private fun TemplateBuilder.injectOptionalParams(geminioRecipe: GeminioRecipe) {
    with(geminioRecipe) {
        if (optionalParams == null) {
            revision = GeminioSdkConstants.DEFAULT_REVISION_VALUE
            category = Category.Other
            formFactor = FormFactor.Mobile
            constraints = emptyList()
            screens = emptyList()
            minApi = GeminioSdkConstants.DEFAULT_MIN_API_VALUE
            minBuildApi = GeminioSdkConstants.DEFAULT_MIN_BUILD_API_VALUE
        } else {
            revision = optionalParams.revision
            category = optionalParams.category.toAndroidStudioTemplateCategory()
            formFactor = optionalParams.formFactor.toAndroidStudioTemplateFormFactor()
            constraints = optionalParams.constraints.map { it.toAndroidStudioTemplateConstraint() }
            screens = optionalParams.screens.map { it.toAndroidStudioTemplateWizardUiContext() }
            minApi = optionalParams.minApi
            minBuildApi = optionalParams.minBuildApi
        }
    }
}

private fun TemplateBuilder.injectWidgets(geminioRecipe: GeminioRecipe): Map<String, AndroidStudioTemplateParameter> {
    val existingParametersMap = mutableMapOf<String, AndroidStudioTemplateParameter>()

    val allParameters: List<AndroidStudioTemplateParameter> = with(geminioRecipe) {
        recipeParameters.map { recipeParameter ->
            val idParameterPair = recipeParameter.toAndroidStudioTemplateIdParameterPair(existingParametersMap)
            existingParametersMap[idParameterPair.id] = idParameterPair.parameter
            idParameterPair.parameter
        }
    }
    val allWidgets = allParameters.mapNotNull { parameter ->
        when (parameter) {
            is StringParameter -> TextFieldWidget(parameter)
            is BooleanParameter -> CheckBoxWidget(parameter)
            is EnumParameter<*> -> EnumWidget(parameter)
            else -> null
        }
    }
    widgets(*allWidgets.toTypedArray())

    return existingParametersMap
}
