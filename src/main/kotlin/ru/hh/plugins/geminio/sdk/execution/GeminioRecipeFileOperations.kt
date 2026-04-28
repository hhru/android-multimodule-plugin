package ru.hh.plugins.geminio.sdk.execution

import java.io.File

/**
 * Minimal file-system contract used by the pure Geminio recipe runner.
 */
internal interface GeminioRecipeFileOperations {

    val createdFiles: Collection<File>
    val filesToOpen: Collection<File>

    fun save(source: String, to: File)

    fun append(source: String, to: File)

    fun createDirectory(at: File)

    fun open(file: File)
}
