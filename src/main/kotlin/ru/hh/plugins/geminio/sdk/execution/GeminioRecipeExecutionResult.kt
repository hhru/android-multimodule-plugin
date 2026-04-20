package ru.hh.plugins.geminio.sdk.execution

import java.io.File

/**
 * Result of Geminio recipe execution.
 *
 * `createdFiles` are later used for Kotlin post-processing, while `filesToOpen` preserves the
 * semantics of explicit `open` and `instantiateAndOpen` commands for a future editor-opening step.
 */
internal data class GeminioRecipeExecutionResult(
    val createdFiles: List<File>,
    val filesToOpen: List<File>,
)
