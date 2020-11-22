package ru.hh.plugins.geminio.template

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
import ru.hh.plugins.geminio.GeminioConstants
import ru.hh.plugins.geminio.model.GeminioRecipe
import ru.hh.plugins.geminio.model.aliases.AndroidStudioTemplate
import ru.hh.plugins.geminio.model.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.model.mapping.toAndroidStudioTemplateCategory
import ru.hh.plugins.geminio.model.mapping.toAndroidStudioTemplateConstraint
import ru.hh.plugins.geminio.model.mapping.toAndroidStudioTemplateFormFactor
import ru.hh.plugins.geminio.model.mapping.toAndroidStudioTemplateIdParameterPair
import ru.hh.plugins.geminio.model.temp_data.GeminioRecipeExecutorData
import ru.hh.plugins.utils.freemarker.FreemarkerConfiguration


/**
 * Build Android Studio [ru.hh.plugins.geminio.model.aliases.AndroidStudioTemplate]
 * from [ru.hh.plugins.geminio.model.GeminioRecipe].
 */
fun geminioTemplate(geminioRecipe: GeminioRecipe): AndroidStudioTemplate = template {
    injectRequiredParams(geminioRecipe)
    injectOptionalParams(geminioRecipe)

    val existingParametersMap = injectWidgets(geminioRecipe)

    recipe = { templateData ->
        geminioRecipe(
            geminioRecipe = geminioRecipe,
            executorData = GeminioRecipeExecutorData(
                moduleTemplateData = templateData as ModuleTemplateData,
                existingParametersMap = existingParametersMap,
                resolvedParamsMap = existingParametersMap.asIterable().associate { entry ->
                    entry.key to entry.value.value
                },
                freemarkerConfiguration = FreemarkerConfiguration(geminioRecipe.freemarkerTemplatesRootDirPath)
            )
        )
    }
}


private fun TemplateBuilder.injectRequiredParams(geminioRecipe: GeminioRecipe) {
    with(geminioRecipe) {
        revision = requiredParams.revision
        name = requiredParams.name
        description = requiredParams.description
    }
}

private fun TemplateBuilder.injectOptionalParams(geminioRecipe: GeminioRecipe) {
    with(geminioRecipe) {
        if (optionalParams == null) {
            category = Category.Other
            formFactor = FormFactor.Mobile
            constraints = emptyList()
            minApi = GeminioConstants.DEFAULT_MIN_API_VALUE
            minBuildApi = GeminioConstants.DEFAULT_MIN_BUILD_API_VALUE
        } else {
            category = optionalParams.category.toAndroidStudioTemplateCategory()
            formFactor = optionalParams.formFactor.toAndroidStudioTemplateFormFactor()
            constraints = optionalParams.constraints.map { it.toAndroidStudioTemplateConstraint() }
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
