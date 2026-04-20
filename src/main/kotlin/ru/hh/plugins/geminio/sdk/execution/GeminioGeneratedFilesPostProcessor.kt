package ru.hh.plugins.geminio.sdk.execution

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.utils.ide.EditorUtils
import ru.hh.plugins.utils.ide.ReformatUtils
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * Applies IDE-visible post-processing to generated files after recipe execution.
 *
 * The sequence intentionally mirrors the old Android Studio template flow:
 * 1. commit documents after recipe writes,
 * 2. reformat/rearrange/save generated files,
 * 3. open explicitly requested files in editors.
 */
internal object GeminioGeneratedFilesPostProcessor {

    fun process(
        project: Project,
        createdFiles: Collection<File>,
        filesToOpen: Collection<File>,
    ) {
        if (createdFiles.isEmpty() && filesToOpen.isEmpty()) {
            return
        }

        measureTimeMillis {
            PsiDocumentManager.getInstance(project).commitAllDocuments()

            if (createdFiles.isNotEmpty()) {
                ReformatUtils.reformatRearrangeAndSave(project, createdFiles)
                PsiDocumentManager.getInstance(project).commitAllDocuments()
            }

            if (filesToOpen.isNotEmpty()) {
                openEditors(project, filesToOpen, true)
            }
        }.also { elapsedTimeMs ->
            HHLogger.d("Geminio generated files post-processing time: $elapsedTimeMs ms")
        }
    }

    private fun openEditors(
        project: Project,
        filesToOpen: Collection<File>,
        select: Boolean,
    ) {
        var last: VirtualFile? = null

        filesToOpen
            .filter(File::exists)
            .mapNotNull { VfsUtil.findFileByIoFile(it, true) }
            .forEach { file ->
                last = file
                EditorUtils.openEditor(project, file)
            }

        if (select && last != null) {
            EditorUtils.selectEditor(project, last)
        }
    }

}
