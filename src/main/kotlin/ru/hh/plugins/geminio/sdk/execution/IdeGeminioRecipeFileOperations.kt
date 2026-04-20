package ru.hh.plugins.geminio.sdk.execution

import com.android.tools.idea.templates.TemplateUtils
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDocumentManager
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * IDE-backed file operations implementation for the pure Geminio recipe runner.
 *
 * The implementation mirrors Android Studio template IO more closely than plain `java.io.File`
 * writes: files are created through VFS, content is written with `setBinaryContent(...)`, and PSI
 * documents are committed right after each write. This keeps IDE/VCS listeners aware of generated
 * files.
 */
internal class IdeGeminioRecipeFileOperations(
    private val project: Project,
) : GeminioRecipeFileOperations {

    private val mutableCreatedFiles = linkedSetOf<File>()
    private val mutableFilesToOpen = linkedSetOf<File>()

    override val createdFiles: Collection<File>
        get() = mutableCreatedFiles

    override val filesToOpen: Collection<File>
        get() = mutableFilesToOpen

    override fun save(source: String, to: File) {
        writeFile(source, to)
        mutableCreatedFiles += to
    }

    override fun append(source: String, to: File) {
        val targetText = if (to.exists()) {
            TemplateUtils.readTextFromDocument(project, to).orEmpty()
        } else {
            String()
        }
        val contents = targetText + (if (targetText.endsWith('\n')) String() else "\n") + source

        writeFile(contents, to)
        mutableCreatedFiles += to
    }

    override fun createDirectory(at: File) {
        ensureDirectoryExists(at)
    }

    override fun open(file: File) {
        mutableFilesToOpen += file
    }

    private fun writeFile(
        contents: String,
        to: File,
    ) {
        val parentDir = ensureDirectoryExists(requireNotNull(to.parentFile) {
            "Parent directory is required for generated file '${to.absolutePath}'"
        })
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(to)
            ?: parentDir.createChildData(this, to.name)

        virtualFile.setBinaryContent(contents.toByteArray(StandardCharsets.UTF_8), -1, -1, this)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
    }

    private fun ensureDirectoryExists(directory: File) = runCatching {
        TemplateUtils.checkedCreateDirectoryIfMissing(directory)
    }.getOrElse { error ->
        throw IllegalStateException("Cannot create directory '${directory.absolutePath}'", error)
    }
}
