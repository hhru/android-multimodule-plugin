package ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined

import com.android.tools.idea.wizard.template.stringParameter
import ru.hh.plugins.extensions.toFormattedModuleName
import ru.hh.plugins.extensions.toPackageNameFromModuleName
import ru.hh.plugins.geminio.sdk.GeminioSdkConstants
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameterConstraint
import ru.hh.plugins.geminio.sdk.template.models.GeminioTemplateParameterData

internal fun PredefinedFeaturesSection.Companion.createModuleNameParameter(): GeminioTemplateParameterData {
    return GeminioTemplateParameterData(
        parameterId = GeminioSdkConstants.FEATURE_MODULE_NAME_PARAMETER_ID,
        parameter = stringParameter {
            name = "Module name"
            help = "The name of creating module without ':' prefix"
            constraints = listOf(
                AndroidStudioTemplateStringParameterConstraint.MODULE,
                AndroidStudioTemplateStringParameterConstraint.UNIQUE,
            )
            default = "feature_my_module"
            visible = { true }
            enabled = { true }
        }
    )
}

internal fun PredefinedFeaturesSection.Companion.createFormattedModuleNameParameter(
    moduleNameParameter: AndroidStudioTemplateStringParameter,
): GeminioTemplateParameterData {
    return GeminioTemplateParameterData(
        parameterId = GeminioSdkConstants.FEATURE_FORMATTED_MODULE_NAME_PARAMETER_ID,
        parameter = stringParameter {
            name = "Module's classes prefix"
            help = "Prefix for classes with module name"
            default = "MyModule"
            suggest = { moduleNameParameter.value.toFormattedModuleName() }
            visible = { true }
            enabled = { true }
        }
    )
}

internal fun PredefinedFeaturesSection.Companion.createPackageNameParameter(
    defaultPackageNamePrefix: String = "ru.hh",
    moduleNameParameter: AndroidStudioTemplateStringParameter,
): GeminioTemplateParameterData {
    return GeminioTemplateParameterData(
        parameterId = GeminioSdkConstants.FEATURE_PACKAGE_NAME_PARAMETER_ID,
        parameter = stringParameter {
            name = "Package name"
            help = "Package name of creating module"
            constraints = listOf(
                AndroidStudioTemplateStringParameterConstraint.PACKAGE,
                AndroidStudioTemplateStringParameterConstraint.UNIQUE,
            )
            default = "$defaultPackageNamePrefix.mymodule"
            suggest = { moduleNameParameter.value.toPackageNameFromModuleName(defaultPackageNamePrefix) }
            visible = { true }
            enabled = { true }
        }
    )
}
