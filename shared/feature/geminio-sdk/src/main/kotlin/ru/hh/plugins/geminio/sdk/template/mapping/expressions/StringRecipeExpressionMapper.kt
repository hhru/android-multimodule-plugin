package ru.hh.plugins.geminio.sdk.template.mapping.expressions

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.activityToLayout
import com.android.tools.idea.wizard.template.camelCaseToUnderlines
import com.android.tools.idea.wizard.template.classToResource
import com.android.tools.idea.wizard.template.fragmentToLayout
import com.android.tools.idea.wizard.template.layoutToActivity
import com.android.tools.idea.wizard.template.layoutToFragment
import com.android.tools.idea.wizard.template.underscoreToCamelCase
import com.intellij.openapi.vfs.VirtualFile
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.CurrentDirOut
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.Dynamic
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.Fixed
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ManifestOut
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ResOut
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ReturnFalse
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.ReturnTrue
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.RootOut
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand.SrcOut
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.ACTIVITY_TO_LAYOUT
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.CAMEL_CASE_TO_UNDERLINES
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.CLASS_TO_RESOURCE
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.FRAGMENT_TO_LAYOUT
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.LAYOUT_TO_ACTIVITY
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.LAYOUT_TO_FRAGMENT
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier.UNDERSCORE_TO_CAMEL_CASE
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameter
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameterStringLambda
import ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateStringParameter

/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameterStringLambda].
 */
internal fun RecipeExpression.toStringLambda(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>,
    parameterId: String
): AndroidStudioTemplateParameterStringLambda {
    return {
        val evaluatedValue = evaluateString(existingParametersMap)
        (existingParametersMap[parameterId] as? AndroidStudioTemplateStringParameter)?.value =
            evaluatedValue ?: String.EMPTY
        evaluatedValue
    }
}

internal fun RecipeExpression.evaluateString(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String? {
    return executeAllCommands(expressionCommands, existingParametersMap)
}

/**
 * Mapping from [ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression]
 * into [ru.hh.plugins.geminio.sdk.template.aliases.AndroidStudioTemplateParameterStringLambda].
 */
internal fun RecipeExpression.evaluateString(
    targetDirectory: VirtualFile,
    moduleTemplateData: ModuleTemplateData,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String? {
    val commands = this.expressionCommands

    val result = StringBuilder()

    for (command in commands) {
        result.append(
            command.toStringValue(
                targetDirectory = targetDirectory,
                moduleTemplateData = moduleTemplateData,
                existingParametersMap = existingParametersMap
            )
        )
    }

    return result.toString().takeIf { it.isNotEmpty() }
}

private fun RecipeExpressionCommand.toStringValue(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String {
    return when (this) {
        is Fixed -> this.value
        is Dynamic -> this.evaluate(existingParametersMap)

        SrcOut,
        ResOut,
        ManifestOut,
        RootOut,
        CurrentDirOut,

        ReturnTrue,
        ReturnFalse -> throw IllegalArgumentException("Unexpected command for string parameter [$this]")
    }
}

private fun RecipeExpressionCommand.toStringValue(
    targetDirectory: VirtualFile,
    moduleTemplateData: ModuleTemplateData,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String {
    val (_, srcOut, resOut, manifestOut, _, _, _, rootOut) = moduleTemplateData

    return when (this) {
        is Fixed -> this.value
        is Dynamic -> this.evaluate(existingParametersMap)

        SrcOut -> "${srcOut.absolutePath}/"
        ResOut -> "${resOut.absolutePath}/"
        ManifestOut -> "${manifestOut.absolutePath}/"
        RootOut -> "${rootOut.absolutePath}/"
        CurrentDirOut -> {
            if (targetDirectory.isDirectory) {
                targetDirectory.path
            } else {
                targetDirectory.parent.path
            }
        }

        ReturnTrue,
        ReturnFalse -> throw IllegalArgumentException("Unexpected command for string value [$this]")
    }
}

private fun Dynamic.evaluate(
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String {
    val result = (existingParametersMap[parameterId] as? AndroidStudioTemplateStringParameter)?.value
        ?: throw IllegalArgumentException("Unknown parameter or not string parameter for string expression [${this.parameterId}]")

    return result.applyModifiers(modifiers)
}

private fun executeAllCommands(
    expressionCommands: List<RecipeExpressionCommand>,
    existingParametersMap: Map<String, AndroidStudioTemplateParameter>
): String? {
    val resultBuilder = StringBuilder()

    for (command in expressionCommands) {
        resultBuilder.append(command.toStringValue(existingParametersMap))
    }

    return resultBuilder.toString().takeIf { it.isNotEmpty() }
}

private fun String.applyModifiers(modifiers: List<RecipeExpressionModifier>): String {
    var result = this
    for (modifier in modifiers) {
        result = when (modifier) {
            ACTIVITY_TO_LAYOUT -> activityToLayout(result)
            FRAGMENT_TO_LAYOUT -> fragmentToLayout(result)
            CLASS_TO_RESOURCE -> classToResource(result)
            CAMEL_CASE_TO_UNDERLINES -> camelCaseToUnderlines(result)
            LAYOUT_TO_ACTIVITY -> layoutToActivity(result)
            LAYOUT_TO_FRAGMENT -> layoutToFragment(result)
            UNDERSCORE_TO_CAMEL_CASE -> underscoreToCamelCase(result)
            RecipeExpressionModifier.UNCAP_FIRST -> result.replaceFirstChar { it.lowercase() }
        }
    }
    return result
}
