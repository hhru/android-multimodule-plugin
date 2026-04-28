package ru.hh.plugins.geminio.sdk.execution

import ru.hh.plugins.geminio.sdk.form.GeminioFormEvaluationContext
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathAlias
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathContext

/**
 * Read-only execution context passed into recipe expression evaluators.
 */
internal class GeminioRecipeEvaluationContext(
    private val templateParameters: Map<String, Any?>,
    private val pathContext: GeminioFormPathContext,
) : GeminioFormEvaluationContext {

    override fun getValue(parameterId: String): Any? {
        return templateParameters[parameterId]
    }

    override fun getPath(pathAlias: GeminioFormPathAlias): String? {
        return pathContext.get(pathAlias)
    }
}
