package ru.hh.plugins.geminio.sdk.recipe.parsers.expressions

import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier
import ru.hh.plugins.geminio.sdk.recipe.parsers.ParsersErrorsFactory.sectionUnknownEnumKeyErrorMessage

private const val SRC_OUT_FOLDER_NAME = "srcOut"
private const val RES_OUT_FOLDER_NAME = "resOut"
private const val MANIFEST_OUT_FOLDER_NAME = "manifestOut"
private const val ROOT_OUT_FOLDER_NAME = "rootOut"
private const val CURRENT_DIR_OUT_FOLDER_NAME = "currentDirOut"

private const val FIXED_TRUE_VALUE = "true"
private const val FIXED_FALSE_VALUE = "false"
private const val COMPARISON_OPERATOR_EQUALS = "=="
private const val COMPARISON_OPERATOR_NOT_EQUALS = "!="
private const val COMPARISON_PARAMETER_GROUP_INDEX = 1
private const val COMPARISON_OPERATOR_GROUP_INDEX = 2
private const val COMPARISON_EXPECTED_VALUE_GROUP_INDEX = 3
private const val WRAPPED_QUOTE_LENGTH = 2

private val BOOLEAN_COMPARISON_REGEX =
    Regex("""^\s*(\$\{[^}]+})\s*(==|!=)\s*(.+?)\s*$""")

private const val CHAR_DYNAMIC_COMMAND_START = '$'
private const val CHAR_DYNAMIC_COMMAND_END = '}'
private const val CHAR_DYNAMIC_COMMAND_SKIP = '{'

private const val CHAR_COMMAND_MODIFIER_START = '.'
private const val CHAR_COMMAND_MODIFIER_END = '('
private const val CHAR_COMMAND_MODIFIER_SKIP = ')'

/**
 * Parser from recipe's expressions declarations into objects.
 */
internal fun String.toRecipeExpression(sectionName: String): RecipeExpression {
    parseComparisonExpression(sectionName)?.let { return it }

    val commands = mutableListOf<RecipeExpressionCommand>()

    var fixed = ""
    var parameterId = ""
    var modifiers = mutableListOf<RecipeExpressionModifier>()
    var modifierName = ""
    var startDynamic = false
    var startModifier = false

    for (char in this) {
        when (char) {
            CHAR_DYNAMIC_COMMAND_START -> {
                if (fixed.isNotEmpty()) {
                    commands += RecipeExpressionCommand.Fixed(fixed)
                }
                fixed = ""
                startDynamic = true
            }

            CHAR_COMMAND_MODIFIER_START -> {
                if (startDynamic) {
                    startModifier = true
                } else {
                    fixed += char
                }
            }

            CHAR_COMMAND_MODIFIER_END -> {
                RecipeExpressionModifier.fromYamlKey(modifierName)
                    ?.let { modifiers.add(it) }
                    ?: throw IllegalArgumentException(
                        sectionUnknownEnumKeyErrorMessage(
                            sectionName = sectionName,
                            key = modifierName,
                            acceptableValues = RecipeExpressionModifier.availableYamlKeys()
                        )
                    )
                modifierName = ""
                startModifier = false
            }

            CHAR_DYNAMIC_COMMAND_END -> {
                startDynamic = false
                commands += when (parameterId) {
                    SRC_OUT_FOLDER_NAME -> {
                        RecipeExpressionCommand.SrcOut
                    }

                    RES_OUT_FOLDER_NAME -> {
                        RecipeExpressionCommand.ResOut
                    }

                    MANIFEST_OUT_FOLDER_NAME -> {
                        RecipeExpressionCommand.ManifestOut
                    }

                    ROOT_OUT_FOLDER_NAME -> {
                        RecipeExpressionCommand.RootOut
                    }

                    CURRENT_DIR_OUT_FOLDER_NAME -> {
                        RecipeExpressionCommand.CurrentDirOut
                    }

                    else -> {
                        RecipeExpressionCommand.Dynamic(
                            parameterId = parameterId,
                            modifiers = modifiers.toList()
                        )
                    }
                }
                parameterId = ""
                modifiers = mutableListOf()
            }

            CHAR_COMMAND_MODIFIER_SKIP, CHAR_DYNAMIC_COMMAND_SKIP -> {
                // do nothing by default
            }

            else -> {
                when {
                    startModifier -> {
                        modifierName += char
                    }

                    startDynamic -> {
                        parameterId += char
                    }

                    else -> {
                        fixed += char
                    }
                }
            }
        }
    }
    if (fixed.isNotEmpty()) {
        commands += when {
            fixed == FIXED_TRUE_VALUE && commands.isEmpty() -> RecipeExpressionCommand.ReturnTrue
            fixed == FIXED_FALSE_VALUE && commands.isEmpty() -> RecipeExpressionCommand.ReturnFalse
            else -> RecipeExpressionCommand.Fixed(fixed)
        }
    }

    return RecipeExpression(commands)
}

private fun String.parseComparisonExpression(sectionName: String): RecipeExpression? {
    val match = BOOLEAN_COMPARISON_REGEX.matchEntire(this) ?: return null
    val parameterExpression = match.groupValues[COMPARISON_PARAMETER_GROUP_INDEX]
    val operator = match.groupValues[COMPARISON_OPERATOR_GROUP_INDEX]
    val expectedValue = match.groupValues[COMPARISON_EXPECTED_VALUE_GROUP_INDEX].trim().removeWrappingQuotes()
    val parameterCommand = parameterExpression.toDynamicComparisonParameter(sectionName)
    val comparisonCommand = operator.toComparisonCommand(parameterCommand, expectedValue)

    return comparisonCommand?.let { command ->
        RecipeExpression(listOf(command))
    }
}

private fun String.toDynamicComparisonParameter(
    sectionName: String,
): RecipeExpressionCommand.Dynamic {
    val expression = toRecipeExpression(sectionName)

    return expression.expressionCommands.singleOrNull() as? RecipeExpressionCommand.Dynamic
        ?: throw IllegalArgumentException(
            "'$sectionName' section: Boolean comparison supports only a single dynamic " +
                "parameter expression [expression: $this]."
        )
}

private fun String.toComparisonCommand(
    parameterCommand: RecipeExpressionCommand.Dynamic,
    expectedValue: String,
): RecipeExpressionCommand? {
    return when (this) {
        COMPARISON_OPERATOR_EQUALS -> RecipeExpressionCommand.EqualTo(parameterCommand, expectedValue)
        COMPARISON_OPERATOR_NOT_EQUALS -> RecipeExpressionCommand.NotEqualTo(parameterCommand, expectedValue)
        else -> null
    }
}

private fun String.removeWrappingQuotes(): String {
    if (length < WRAPPED_QUOTE_LENGTH) {
        return this
    }

    return when {
        startsWith('"') && endsWith('"') -> substring(1, lastIndex)
        startsWith('\'') && endsWith('\'') -> substring(1, lastIndex)
        else -> this
    }
}
