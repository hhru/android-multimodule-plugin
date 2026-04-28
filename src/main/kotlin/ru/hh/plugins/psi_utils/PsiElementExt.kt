package ru.hh.plugins.psi_utils

import com.intellij.ide.util.EditorHelper
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiBinaryFile
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.refactoring.copy.CopyHandler

fun PsiElement.reformatWithCodeStyle() {
    CodeStyleManager.getInstance(project).reformat(this)
}

fun PsiElement.openInEditor() {
    CopyHandler.updateSelectionInActiveProjectView(this, this.project, true)
    if (this !is PsiBinaryFile) {
        EditorHelper.openInEditor(this)
        ToolWindowManager.getInstance(project).activateEditorComponent()
    }
}
