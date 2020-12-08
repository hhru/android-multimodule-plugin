package ru.hh.plugins.geminio.sdk.template.mapping

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.activityToLayout
import com.android.tools.idea.wizard.template.camelCaseToUnderlines
import com.android.tools.idea.wizard.template.classToResource
import com.android.tools.idea.wizard.template.fragmentToLayout
import com.android.tools.idea.wizard.template.layoutToActivity
import com.android.tools.idea.wizard.template.layoutToFragment
import com.android.tools.idea.wizard.template.underlinesToCamelCase
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.RecipeExpression.Command.*
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateBooleanParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameterBooleanLambda
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameterStringLambda
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameter
import ru.hh.plugins.geminio.sdk.recipe.enums.GeminioRecipeExpressionModifier.*


fun RecipeExpression.toBooleanLambda(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameterBooleanLambda {
    return { evaluateBoolean(existingParametersMap) }
}

fun RecipeExpression.evaluateBoolean(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): Boolean {
    return when (expressionCommands.size) {
        0 -> true
        1 -> expressionCommands[0].resolveBooleanValue(existingParametersMap)
        else -> throw IllegalArgumentException("Unexpected commands for boolean parameter evaluation [$expressionCommands]")
    }
}


fun RecipeExpression.toStringLambda(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): AndroidStudioTemplateParameterStringLambda {
    val commands = this.expressionCommands

    return {
        val result = StringBuilder()

        for (command in commands) {
            result.append(command.toStringValue(existingParametersMap))
        }

        result.toString().takeIf { it.isNotEmpty() }
    }
}

fun RecipeExpression.evaluateString(
    moduleTemplateData: ModuleTemplateData,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String? {
    val commands = this.expressionCommands

    val result = StringBuilder()

    for (command in commands) {
        result.append(command.toStringValue(moduleTemplateData, existingParametersMap))
    }

    return result.toString().takeIf { it.isNotEmpty() }
}


private fun RecipeExpression.Command.resolveBooleanValue(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): Boolean {
    return when (this) {
        is Dynamic -> {
            val parameter = existingParametersMap[this.parameterId] as? AndroidStudioTemplateBooleanParameter
                ?: throw IllegalArgumentException(
                    "Unknown parameter or not boolean parameter for boolean expression [id: ${this.parameterId}]"
                )

            parameter.value
        }

        ReturnTrue -> {
            true
        }

        ReturnFalse -> {
            false
        }

        is Fixed,
        is SrcOut,
        is ResOut -> {
            throw IllegalArgumentException("Unexpected command for boolean parameter [$this]")
        }
    }
}

private fun RecipeExpression.Command.toStringValue(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String {
    return when (this) {
        is Fixed -> this.value
        is Dynamic -> this.evaluate(existingParametersMap)

        is SrcOut,
        is ResOut,
        ReturnTrue,
        ReturnFalse -> throw IllegalArgumentException("Unexpected command for string parameter [$this]")
    }
}

private fun RecipeExpression.Command.toStringValue(
    moduleTemplateData: ModuleTemplateData,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String {
    val (_, srcOut, resOut, _) = moduleTemplateData

    return when (this) {
        is Fixed -> this.value
        is Dynamic -> this.evaluate(existingParametersMap)
        is SrcOut -> "${srcOut.absolutePath}/"
        is ResOut -> "${resOut.absolutePath}/"

        ReturnTrue,
        ReturnFalse -> throw IllegalArgumentException("Unexpected command for string value [$this]")
    }
}

private fun Dynamic.evaluate(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String {
    var result = (existingParametersMap[parameterId] as? AndroidStudioTemplateStringParameter)?.value
        ?: throw IllegalArgumentException("Unknown parameter or not string parameter for string expression [${this.parameterId}]")

    for (modifier in modifiers) {
        result = when (modifier) {
            ACTIVITY_TO_LAYOUT -> activityToLayout(result)
            FRAGMENT_TO_LAYOUT -> fragmentToLayout(result)
            CLASS_TO_RESOURCE -> classToResource(result)
            CAMEL_CASE_TO_UNDERLINES -> camelCaseToUnderlines(result)
            LAYOUT_TO_ACTIVITY -> layoutToActivity(result)
            LAYOUT_TO_FRAGMENT -> layoutToFragment(result)
            UNDERLINES_TO_CAMEL_CASE -> underlinesToCamelCase(result)
        }
    }

    return result
}