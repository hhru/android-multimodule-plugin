package ru.hh.plugins.geminio.sdk.template.mapping.widgets.predefined

import com.android.tools.idea.wizard.template.stringParameter
import ru.hh.plugins.geminio.sdk.recipe.models.predefined.PredefinedFeaturesSection
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameterConstraint


internal fun PredefinedFeaturesSection.Companion.createModuleNameParameter(): AndroidStudioTemplateStringParameter {
    return stringParameter {
        name = "Module name"
        help = "The name of creating module without ':' prefix"
        constraints = listOf(
            AndroidStudioTemplateStringParameterConstraint.MODULE,
            AndroidStudioTemplateStringParameterConstraint.UNIQUE,
        )
        default = "mymodule"
        visible = { true }
        enabled = { true }
    }
}

internal fun PredefinedFeaturesSection.Companion.createPackageNameParameter(
    defaultPackageNamePrefix: String = "ru.hh",
    moduleNameParameter: AndroidStudioTemplateStringParameter,
): AndroidStudioTemplateParameter {
    return stringParameter {
        name = "Package name"
        help = "Package name of creating module"
        constraints = listOf(
            AndroidStudioTemplateStringParameterConstraint.PACKAGE,
            AndroidStudioTemplateStringParameterConstraint.UNIQUE,
        )
        default = "${defaultPackageNamePrefix}.mymodule"
        suggest = { "${defaultPackageNamePrefix}.${moduleNameParameter.value}" }
        visible = { true }
        enabled = { true }
    }
}