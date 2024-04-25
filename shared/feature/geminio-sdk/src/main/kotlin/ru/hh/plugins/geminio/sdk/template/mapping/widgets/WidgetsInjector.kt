package ru.hh.plugins.geminio.sdk.template.mapping.widgets

import com.android.tools.idea.wizard.template.BooleanParameter
import com.android.tools.idea.wizard.template.CheckBoxWidget
import com.android.tools.idea.wizard.template.EnumParameter
import com.android.tools.idea.wizard.template.EnumWidget
import com.android.tools.idea.wizard.template.StringParameter
import com.android.tools.idea.wizard.template.TextFieldWidget
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_MODULE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.FEATURE_SOURCE_SET_PARAMETER_ID
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants.GLOBALS_SHOW_HIDDEN_VALUES_ID
import ru.hh.plugins.geminio.sdk.recipe.models.GeminioRecipe
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeature
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeatureParameter
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBuilder
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals.toGeminioTemplateParameterData
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals.toShowHiddenGlobalsParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.parameters.toGeminioTemplateParameterData
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined.createFormattedModuleNameParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined.createSourceCodeFolderName
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined.createModuleNameParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined.createPackageNameParameter
import ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined.createSourceSetParameter
import ru.hh.plugins.geminio.sdk.template.models.GeminioRecipeParametersData
import ru.hh.plugins.geminio.sdk.template.models.GeminioTemplateParameterData

/**
 * Injects parameters from [ru.hh.plugins.geminio.sdk.recipe.models.widgets.WidgetsSection]
 * and [ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSection]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBuilder].
 */
internal fun AndroidStudioTemplateBuilder.injectWidgets(
    recipe: GeminioRecipe
): Map<String, AndroidStudioTemplateParameter> {
    val parametersData = recipe.toParametersData()

    val allWidgets = parametersData.templateParameters.mapNotNull { parameterData ->
        when (parameterData.parameter) {
            is StringParameter -> TextFieldWidget(parameterData.parameter)
            is BooleanParameter -> CheckBoxWidget(parameterData.parameter)
            is EnumParameter<*> -> EnumWidget(parameterData.parameter)
            else -> null
        }
    }
    widgets(*allWidgets.toTypedArray())

    return parametersData.existingParametersMap
}

private fun GeminioRecipe.toParametersData(): GeminioRecipeParametersData {
    val existingParametersMap = mutableMapOf<String, AndroidStudioTemplateParameter>()

    val allParameters = mutableListOf<GeminioTemplateParameterData>()

    val moduleCreationParams = predefinedFeaturesSection.features[PredefinedFeature.ENABLE_MODULE_CREATION_PARAMS]
        as? PredefinedFeatureParameter.ModuleCreationParameter
    if (moduleCreationParams != null) {
        val moduleNameParameterData = PredefinedFeaturesSection.createModuleNameParameter()
        val moduleNameStringParameter = moduleNameParameterData.parameter as AndroidStudioTemplateStringParameter
        val formattedModuleNameParameterData = PredefinedFeaturesSection.createFormattedModuleNameParameter(
            moduleNameParameter = moduleNameStringParameter
        )
        val packageNameParameterData = PredefinedFeaturesSection.createPackageNameParameter(
            defaultPackageNamePrefix = moduleCreationParams.defaultPackageNamePrefix,
            moduleNameParameter = moduleNameStringParameter
        )
        val sourceSetParameterData = PredefinedFeaturesSection.createSourceSetParameter(
            defaultSourceSet = moduleCreationParams.defaultSourceSet,
        )
        val codeSourceSetFolderData = PredefinedFeaturesSection.createSourceCodeFolderName(
            defaultSourceCodeFolderName = moduleCreationParams.defaultSourceCodeFolderName,
        )

        allParameters += moduleNameParameterData
        allParameters += formattedModuleNameParameterData
        allParameters += packageNameParameterData
        allParameters += sourceSetParameterData
        allParameters += codeSourceSetFolderData

        existingParametersMap[FEATURE_MODULE_NAME_PARAMETER_ID] = moduleNameParameterData.parameter
        existingParametersMap[FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID] = formattedModuleNameParameterData.parameter
        existingParametersMap[FEATURE_PACKAGE_NAME_PARAMETER_ID] = packageNameParameterData.parameter
        existingParametersMap[FEATURE_SOURCE_SET_PARAMETER_ID] = sourceSetParameterData.parameter
        existingParametersMap[FEATURE_DEFAULT_SOURCE_CODE_FOLDER_PARAMETER_ID] = codeSourceSetFolderData.parameter
    }

    allParameters += widgetsSection.parameters.map { widgetParameter ->
        val parameterData = widgetParameter.toGeminioTemplateParameterData(existingParametersMap)
        existingParametersMap[widgetParameter.id] = parameterData.parameter
        parameterData
    }

    if (globalsSection.parameters.isNotEmpty()) {
        val showHiddenGlobalsParameterData = globalsSection.toShowHiddenGlobalsParameter(
            showHiddenValuesId = GLOBALS_SHOW_HIDDEN_VALUES_ID,
            existingParametersMap = existingParametersMap
        )
        allParameters += showHiddenGlobalsParameterData
        existingParametersMap[GLOBALS_SHOW_HIDDEN_VALUES_ID] = showHiddenGlobalsParameterData.parameter

        allParameters += globalsSection.parameters.map { globalsParameter ->
            val parameterData = globalsParameter.toGeminioTemplateParameterData(
                showHiddenValuesId = GLOBALS_SHOW_HIDDEN_VALUES_ID,
                existingParametersMap = existingParametersMap
            )
            existingParametersMap[globalsParameter.id] = parameterData.parameter
            parameterData
        }
    }

    return GeminioRecipeParametersData(
        templateParameters = allParameters,
        existingParametersMap = existingParametersMap
    )
}
