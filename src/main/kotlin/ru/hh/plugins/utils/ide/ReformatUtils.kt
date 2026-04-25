package ru.hh.plugins.utils.ide

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.arrangement.engine.ArrangementEngine
import java.io.File

/**
 * Utilities for IDE code style post-processing.
 *
 * Copied from Android Studio's `ReformatUtil` and kept locally to avoid depending on Android
 * plugin implementation details in Geminio runtime code.
 */
internal object ReformatUtils {

    fun reformatRearrangeAndSave(
        project: Project,
        files: Iterable<File>,
    ) {
        WriteCommandAction.runWriteCommandAction(project) {
            files.asSequence()
                .filter(File::isFile)
                .filterNot { it.name.startsWith("gradlew") }
                .mapNotNull(LocalFileSystem.getInstance()::findFileByIoFile)
                .forEach { virtualFile ->
                    reformatAndRearrange(project, virtualFile, keepDocumentLocked = true)
                    FileDocumentManager.getInstance().getDocument(virtualFile)?.let { document ->
                        FileDocumentManager.getInstance().saveDocument(document)
                    }
                }
        }
    }

    @JvmOverloads
    fun reformatAndRearrange(
        project: Project,
        virtualFile: VirtualFile,
        psiElement: PsiElement? = null,
        keepDocumentLocked: Boolean = false,
    ) {
        ApplicationManager.getApplication().assertWriteAccessAllowed()

        val document = FileDocumentManager.getInstance().getDocument(virtualFile)
            ?: return

        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        psiDocumentManager.commitDocument(document)

        val psiFile = psiDocumentManager.getPsiFile(document)
            ?: return

        var textRange = if (psiElement == null) {
            psiFile.textRange
        } else {
            psiElement.textRange
        }

        CodeStyleManager.getInstance(project)
            .reformatRange(psiFile, textRange.startOffset, textRange.endOffset)

        textRange = if (psiElement == null) {
            psiFile.textRange
        } else {
            psiElement.textRange
        }

        psiDocumentManager.doPostponedOperationsAndUnblockDocument(document)
        project.getService(ArrangementEngine::class.java).arrange(psiFile, setOf(textRange))

        if (keepDocumentLocked) {
            psiDocumentManager.commitDocument(document)
            PsiDocumentManager.getInstance(project).reparseFiles(listOf(virtualFile), false)
        }
    }
}
