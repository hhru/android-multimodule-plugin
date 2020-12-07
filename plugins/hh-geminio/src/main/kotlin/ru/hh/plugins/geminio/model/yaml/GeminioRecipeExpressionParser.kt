package ru.hh.plugins.geminio.model.yaml

import ru.hh.plugins.geminio.sdk.model.recipe.RecipeExpression
import ru.hh.plugins.geminio.sdk.model.enums.GeminioRecipeExpressionModifier


/**
 * Parser from recipe's expressions declarations into objects.
 */
class GeminioRecipeExpressionParser {

    companion object {
        private const val SRC_OUT_FOLDER_NAME = "srcOut"
        private const val RES_OUT_FOLDER_NAME = "resOut"

        private const val FIXED_TRUE_VALUE = "true"
        private const val FIXED_FALSE_VALUE = "false"

        private const val CHAR_DYNAMIC_COMMAND_START = '$'
        private const val CHAR_DYNAMIC_COMMAND_END = '}'
        private const val CHAR_DYNAMIC_COMMAND_SKIP = '{'

        private const val CHAR_COMMAND_MODIFIER_START = '.'
        private const val CHAR_COMMAND_MODIFIER_END = '('
        private const val CHAR_COMMAND_MODIFIER_SKIP = ')'
    }


    fun parseExpression(expressionString: String): RecipeExpression {
        val commands = mutableListOf<RecipeExpression.Command>()

        var fixed = ""
        var parameterId = ""
        var modifiers = mutableListOf<GeminioRecipeExpressionModifier>()
        var modifierName = ""
        var startDynamic = false
        var startModifier = false

        for (char in expressionString) {
            when (char) {
                CHAR_DYNAMIC_COMMAND_START -> {
                    if (fixed.isNotEmpty()) {
                        commands += RecipeExpression.Command.Fixed(fixed)
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
                    GeminioRecipeExpressionModifier.fromYamlKey(modifierName)?.let { modifiers.add(it) }
                    modifierName = ""
                    startModifier = false
                }

                CHAR_DYNAMIC_COMMAND_END -> {
                    startDynamic = false
                    commands += when (parameterId) {
                        SRC_OUT_FOLDER_NAME -> {
                            RecipeExpression.Command.SrcOut(
                                modifiers = modifiers.toList()
                            )
                        }

                        RES_OUT_FOLDER_NAME -> {
                            RecipeExpression.Command.ResOut(
                                modifiers = modifiers.toList()
                            )
                        }

                        else -> {
                            RecipeExpression.Command.Dynamic(
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
                fixed == FIXED_TRUE_VALUE && commands.isEmpty() -> RecipeExpression.Command.ReturnTrue
                fixed == FIXED_FALSE_VALUE && commands.isEmpty() -> RecipeExpression.Command.ReturnFalse
                else -> RecipeExpression.Command.Fixed(fixed)
            }
        }

        return RecipeExpression(commands)
    }

}