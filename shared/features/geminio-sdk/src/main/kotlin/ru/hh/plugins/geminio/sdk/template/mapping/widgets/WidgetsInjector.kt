package ru.hh.plugins.geminio.sdk.template.mapping.widgets

import com.android.tools.idea.wizard.template.BooleanParameter
import com.android.tools.idea.wizard.template.CheckBoxWidget
import com.android.tools.idea.wizard.template.EnumParameter
import com.android.tools.idea.wizard.template.EnumWidget
import com.android.tools.idea.wizard.template.StringParameter
import com.android.tools.idea.wizard.template.TextFieldWidget
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBuilder
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals.toAndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals.toShowHiddenGlobalsParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.parameters.toAndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined.createModuleNameParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined.createPackageNameParameter
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeParametersData


private const val GLOBALS_SHOW_HIDDEN_VALUES_ID = "__showHiddenGlobals"


/**
 * Injects parameters from [ru.hh.plugins.geminio.sdk.recipe.models.widgets.WidgetsSection]
 * and [ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSection]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBuilder].
 */
internal fun AndroidStudioTemplateBuilder.injectWidgets(
    recipe: GeminioRecipe
): Map<String, AndroidStudioTemplateParameter> {
    val parametersData = recipe.toParametersData()

    val allWidgets = parametersData.templateParameters.mapNotNull { parameter ->
        when (parameter) {
            is StringParameter -> TextFieldWidget(parameter)
            is BooleanParameter -> CheckBoxWidget(parameter)
            is EnumParameter<*> -> EnumWidget(parameter)
            else -> null
        }
    }
    widgets(*allWidgets.toTypedArray())

    return parametersData.existingParametersMap
}


private fun GeminioRecipe.toParametersData(): GeminioRecipeParametersData {
    val existingParametersMap = mutableMapOf<String, AndroidStudioTemplateParameter>()

    val allParameters = mutableListOf<AndroidStudioTemplateParameter>()

    if (predefinedFeaturesSection.features.contains(PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS)) {
        val moduleNameParameter = PredefinedFeaturesSection.createModuleNameParameter()
        val packageNameParameter = PredefinedFeaturesSection.createPackageNameParameter(
            moduleNameParameter = moduleNameParameter
        )
        allParameters += moduleNameParameter
        allParameters += packageNameParameter

        existingParametersMap[GeminioSdkConstants.FEATURE_MODULE_NAME_PARAMETER_ID] = moduleNameParameter
        existingParametersMap[GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID] = packageNameParameter
    }

    allParameters += widgetsSection.parameters.map { widgetParameter ->
        val parameter = widgetParameter.toAndroidStudioTemplateParameter(existingParametersMap)
        existingParametersMap[widgetParameter.id] = parameter
        parameter
    }

    if (globalsSection.parameters.isNotEmpty()) {
        val showHiddenGlobalsParameter = globalsSection.toShowHiddenGlobalsParameter(
            showHiddenValuesId = GLOBALS_SHOW_HIDDEN_VALUES_ID,
            existingParametersMap = existingParametersMap
        )
        allParameters += showHiddenGlobalsParameter
        existingParametersMap[GLOBALS_SHOW_HIDDEN_VALUES_ID] = showHiddenGlobalsParameter

        allParameters += globalsSection.parameters.map { globalsParameter ->
            val parameter = globalsParameter.toAndroidStudioTemplateParameter(
                showHiddenValuesId = GLOBALS_SHOW_HIDDEN_VALUES_ID,
                existingParametersMap = existingParametersMap
            )
            existingParametersMap[globalsParameter.id] = parameter
            parameter
        }
    }

    return GeminioRecipeParametersData(
        templateParameters = allParameters,
        existingParametersMap = existingParametersMap
    )
}