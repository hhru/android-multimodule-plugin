package ru.hh.plugins.utils.ide

import com.intellij.ide.impl.ProjectViewSelectInPaneTarget
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

/**
 * Utils for code editor actions.
 * Copied from [Android plugin's EditorUtil](https://cs.android.com/android-studio/platform/tools/adt/idea/+/mirror-goog-studio-main:android/src/org/jetbrains/android/uipreview/EditorUtil.kt)
 */
internal object EditorUtils {

    /**
     * Opens the specified file in the editor
     *
     * @param project The project which contains the given file.
     * @param vFile The file to open
     */
    fun openEditor(project: Project, vFile: VirtualFile) {
        FileEditorManager.getInstance(project).openEditor(
            OpenFileDescriptor(project, vFile),
            true
        )
    }

    /**
     * Selects the specified file in the project view.
     * **Note:** Must be called with read access.
     *
     * @param project the project
     * @param file the file to select
     */
    fun selectEditor(project: Project, file: VirtualFile) {
        ApplicationManager.getApplication().assertReadAccessAllowed()

        val psiFile = PsiManager.getInstance(project).findFile(file) ?: return
        val currentPane = ProjectView.getInstance(project).currentProjectViewPane ?: return

        ProjectViewSelectInPaneTarget(project, currentPane, true)
            .select(psiFile, false)
    }
}
