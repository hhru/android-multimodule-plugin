package ru.hh.plugins.geminio.sdk.helpers

import ru.hh.plugins.geminio.sdk.execution.GeminioRecipeEvaluationContext
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathContext
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpression
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionCommand
import ru.hh.plugins.geminio.sdk.recipe.models.expressions.RecipeExpressionModifier
import ru.hh.plugins.geminio.sdk.execution.evaluateString as evaluateStringExpression

internal object GeminioExpressionUtils {

    fun List<RecipeExpressionCommand>.toExpression(): RecipeExpression {
        return RecipeExpression(this)
    }

    fun RecipeExpression.evaluateString(
        evaluationContext: GeminioRecipeEvaluationContext,
    ): String? = evaluateStringExpression(evaluationContext)

    fun createParametersMap(
        includeModule: Boolean = true,
        className: String = "BlankFragment",
    ): Map<String, Any?> {
        return mapOf(
            "className" to className,
            "includeModule" to includeModule,
        )
    }

    fun createEvaluationContext(
        parameters: Map<String, Any?> = createParametersMap(),
    ): GeminioRecipeEvaluationContext {
        return GeminioRecipeEvaluationContext(
            templateParameters = parameters,
            pathContext = GeminioFormPathContext(
                srcOut = "/Project/src/main/kotlin/com/example/mylibrary",
                resOut = "/Project/src/main/res",
                manifestOut = "/Project/src/main",
                rootOut = "/Project",
                currentDirOut = "/Project/src/main/kotlin/com/example/mylibrary",
            ),
        )
    }

    fun getEvaluatedValue(className: String, modifier: RecipeExpressionModifier): String? {
        val expression = listOf(
            RecipeExpressionCommand.Dynamic(
                parameterId = "className",
                modifiers = listOf(
                    modifier
                )
            )
        ).toExpression()

        return expression.evaluateString(createEvaluationContext(createParametersMap(className = className)))
    }
}
