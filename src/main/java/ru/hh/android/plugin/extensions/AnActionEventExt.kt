package ru.hh.android.plugin.extensions

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull


fun AnActionEvent.getSelectedPsiElement(): PsiElement? = dataContext.getData(PlatformDataKeys.PSI_ELEMENT)

fun AnActionEvent.canReachKotlinDataClass(): Boolean {
    return when {
        ktClassFromEditor?.isData() == true -> true
        selectedKtClass?.isData() == true -> true
        else -> false
    }
}

fun AnActionEvent.getKotlinDataClass(): KtClass? {
    return (ktClassFromEditor ?: selectedKtClass)?.takeIf { it.isData() }
}


val AnActionEvent.androidFacet: AndroidFacet?
    get() = getSelectedPsiElement()?.module?.androidFacet

private val AnActionEvent.ktClassFromEditor: KtClass?
    get() {
        val currentPsiFile = getData(LangDataKeys.PSI_FILE)
        val editor = getData(PlatformDataKeys.EDITOR)

        if (currentPsiFile == null || editor == null) {
            return null
        }

        val offset = editor.caretModel.offset
        val psiElement = currentPsiFile.findElementAt(offset)

        return PsiTreeUtil.getParentOfType(psiElement, KtClass::class.java)
    }

private val AnActionEvent.selectedKtClass: KtClass?
    get() = (getSelectedPsiElement() as? KtFile)?.classes?.firstIsInstanceOrNull()