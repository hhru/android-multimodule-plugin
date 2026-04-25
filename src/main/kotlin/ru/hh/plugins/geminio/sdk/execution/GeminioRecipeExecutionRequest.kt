package ru.hh.plugins.geminio.sdk.execution

import com.intellij.openapi.project.Project
import ru.hh.plugins.geminio.sdk.form.GeminioFormPathContext
import ru.hh.plugins.geminio.templating.FreemarkerConfiguration

/**
 * Immutable input for pure Geminio recipe execution.
 *
 * It contains only project-level services, resolved template parameters and path aliases required
 * by the recipe commands. The request deliberately avoids Android Studio wizard/template classes.
 */
internal data class GeminioRecipeExecutionRequest(
    val project: Project,
    val pathContext: GeminioFormPathContext,
    val templateParameters: Map<String, Any?>,
    val freemarkerConfiguration: FreemarkerConfiguration,
)
