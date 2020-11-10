package ru.hh.android.plugins.garcon.extensions

import com.intellij.ide.IdeView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.psi.KtClass


fun AnActionEvent.getXmlFileFromEditorOrSelection(): XmlFile? {
    return getXmlPsiFileFromEditor() ?: getSelectedXmlPsiFile()
}

fun AnActionEvent.getSelectedPsiFile(): PsiFile? {
    return getData(LangDataKeys.PSI_FILE)
}

fun AnActionEvent.getIdeView(): IdeView? = LangDataKeys.IDE_VIEW.getData(dataContext)

fun AnActionEvent.getKtClassFromEditor(): KtClass? {
    val currentPsiFile = getData(LangDataKeys.PSI_FILE)
    val editor = getData(PlatformDataKeys.EDITOR)

    if (currentPsiFile == null || editor == null) {
        return null
    }

    val offset = editor.caretModel.offset
    val psiElement = currentPsiFile.findElementAt(offset)

    return PsiTreeUtil.getParentOfType(psiElement, KtClass::class.java)
}


private fun AnActionEvent.getSelectedXmlPsiFile(): XmlFile? {
    return getSelectedPsiFile() as? XmlFile
}

private fun AnActionEvent.getXmlPsiFileFromEditor(): XmlFile? {
    val currentPsiFile = getSelectedPsiFile()
    val editor = getData(PlatformDataKeys.EDITOR)

    if (currentPsiFile == null || editor == null) {
        this.presentation.isEnabled = false
        return null
    }

    val offset = editor.caretModel.offset
    val psiElement = currentPsiFile.findElementAt(offset)

    return PsiTreeUtil.getParentOfType(psiElement, XmlFile::class.java)
}