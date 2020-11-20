package ru.hh.plugins.geminio.model.yaml

import ru.hh.plugins.geminio.model.GeminioRecipe
import ru.hh.plugins.geminio.model.GeminioRecipeExpressionModifier


/**
 * Converter from recipe's expressions declarations into objects.
 */
class GeminioRecipeExpressionReader {

    companion object {
        private const val SRC_OUT_FOLDER_NAME = "srcOut"
        private const val RES_OUT_FOLDER_NAME = "resOut"
    }


    fun parseExpression(expressionString: String): GeminioRecipe.RecipeExpression {
        val commands = mutableListOf<GeminioRecipe.RecipeExpression.Command>()

        var fixed = ""
        var parameterId = ""
        var modifiers = mutableListOf<GeminioRecipeExpressionModifier>()
        var modifierName = ""
        var startDynamic = false
        var startModifier = false

        for (char in expressionString) {
            when (char) {
                '$' -> {
                    if (fixed.isNotEmpty()) {
                        commands += GeminioRecipe.RecipeExpression.Command.Fixed(fixed)
                    }
                    fixed = ""
                    startDynamic = true
                }

                '.' -> {
                    if (startDynamic) {
                        startModifier = true
                    } else {
                        fixed += char
                    }
                }

                '(' -> {
                    GeminioRecipeExpressionModifier.fromYamlKey(modifierName)?.let { modifiers.add(it) }
                    modifierName = ""
                    startModifier = false
                }

                '}' -> {
                    startDynamic = false
                    commands += when (parameterId) {
                        SRC_OUT_FOLDER_NAME -> {
                            GeminioRecipe.RecipeExpression.Command.SrcOut(
                                modifiers = modifiers.toList()
                            )
                        }

                        RES_OUT_FOLDER_NAME -> {
                            GeminioRecipe.RecipeExpression.Command.ResOut(
                                modifiers = modifiers.toList()
                            )
                        }

                        else -> {
                            GeminioRecipe.RecipeExpression.Command.Dynamic(
                                parameterId = parameterId,
                                modifiers = modifiers.toList()
                            )
                        }
                    }
                    parameterId = ""
                    modifiers = mutableListOf()
                }

                ')', '{' -> {
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
                fixed == "true" && commands.isEmpty() -> GeminioRecipe.RecipeExpression.Command.ReturnTrue
                fixed == "false" && commands.isEmpty() -> GeminioRecipe.RecipeExpression.Command.ReturnFalse
                else -> GeminioRecipe.RecipeExpression.Command.Fixed(fixed)
            }
        }

        return GeminioRecipe.RecipeExpression(commands)
    }

}