package ru.hh.plugins.geminio.sdk.template.mapping.widgets.globals

import com.android.tools.idea.wizard.template.stringParameter
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.toBooleanLambda
import ru.hh.plugins.geminio.sdk.template.mapping.expressions.toStringLambda


/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.globals.GlobalsSectionParameter.StringParameter]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter].
 */
internal fun GlobalsSectionParameter.StringParameter.toAndroidStudioTemplateParameter(
    showHiddenValuesId: String,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameter {
    val globalsParameter = this

    return stringParameter {
        name = globalsParameter.id
        help = globalsParameter.id

        this.default = String.EMPTY
        this.constraints = emptyList()
        this.suggest = globalsParameter.value.toStringLambda(existingParametersMap)

        this.visible = RecipeExpression.globalsVisibilityExpression(showHiddenValuesId)
            .toBooleanLambda(existingParametersMap)
        this.enabled = { true }
    }
}