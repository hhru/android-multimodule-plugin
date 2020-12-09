package ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals

import com.android.tools.idea.wizard.template.booleanParameter
import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSection
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter


internal fun GlobalsSection.toShowHiddenGlobalsParameter(
    showHiddenValuesId: String,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameter {
    check(existingParametersMap[showHiddenValuesId] == null) {
        "You cannot have template parameter with id='$showHiddenValuesId' with 'globals' section in your recipe.yaml. " +
                "Rename your parameter from widgets section."
    }

    return booleanParameter {
        name = "Show hidden globals values"
        help = "Shows values of 'globals' section"

        default = false
        visible = { true }
        enabled = { true }
    }
}