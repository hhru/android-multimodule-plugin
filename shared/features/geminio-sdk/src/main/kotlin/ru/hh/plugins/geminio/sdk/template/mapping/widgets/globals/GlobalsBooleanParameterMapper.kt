package ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals

import com.android.tools.idea.wizard.template.booleanParameter
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.evaluateBoolean
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.toBooleanLambda


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter.BooleanParameter]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter].
 */
internal fun GlobalsSectionParameter.BooleanParameter.toAndroidStudioTemplateParameter(
    showHiddenValuesId: String,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameter {
    val globalsParameter = this

    return booleanParameter {
        name = globalsParameter.id
        help = globalsParameter.id

        this.default = globalsParameter.value.evaluateBoolean(existingParametersMap)
        this.visible = RecipeExpression.globalsVisibilityExpression(showHiddenValuesId)
            .toBooleanLambda(existingParametersMap)
        this.enabled = { true }
    }
}