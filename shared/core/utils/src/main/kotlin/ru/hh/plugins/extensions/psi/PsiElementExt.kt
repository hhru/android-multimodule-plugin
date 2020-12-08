package ru.hh.plugins.extensions.psi

import com.android.tools.idea.util.androidFacet
import com.intellij.ide.util.EditorHelper
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiBinaryFile
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.refactoring.copy.CopyHandler
import org.jetbrains.kotlin.idea.util.module
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.extensions.packageName


val PsiElement.androidManifestPackageName: String
    get() = module?.androidFacet?.run { this.packageName } ?: String.EMPTY

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