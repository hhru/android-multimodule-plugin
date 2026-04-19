package ru.hh.plugins.extensions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

fun AnActionEvent.getSelectedPsiElement(): PsiElement? = getData(PlatformDataKeys.PSI_ELEMENT)

@Suppress("detekt.UseCheckOrError")
fun AnActionEvent.getTargetDirectory(): VirtualFile {
    val currentVirtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext)

    return when {
        currentVirtualFile == null -> {
            throw IllegalStateException("You should select some file for code generation")
        }

        currentVirtualFile.isDirectory.not() -> {
            // If the user selected a simulated folder entry (eg "Manifests"), there will be no target directory
            currentVirtualFile.parent
        }

        else -> {
            currentVirtualFile
        }
    }
}
