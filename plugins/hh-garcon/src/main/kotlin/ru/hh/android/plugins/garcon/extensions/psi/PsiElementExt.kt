package ru.hh.android.plugins.garcon.extensions.psi

import com.android.tools.idea.util.androidFacet
import com.intellij.ide.util.EditorHelper
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiBinaryFile
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.refactoring.copy.CopyHandler
import org.jetbrains.android.dom.manifest.cachedValueFromPrimaryManifest
import org.jetbrains.android.dom.manifest.packageName
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.util.module
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY


fun PsiElement.reformatWithCodeStyle() {
    module?.project?.let { CodeStyleManager.getInstance(it).reformat(this) }
}

fun PsiElement.openInEditor() {
    CopyHandler.updateSelectionInActiveProjectView(this, this.project, true)
    if (this !is PsiBinaryFile) {
        EditorHelper.openInEditor(this)
        ToolWindowManager.getInstance(project).activateEditorComponent()
    }
}

val PsiElement.androidManifestPackageName: String?
    get() = module?.androidFacet?.run { this.packageName }

val AndroidFacet.packageName: String get() = cachedValueFromPrimaryManifest { this.packageName }.value ?: String.EMPTY