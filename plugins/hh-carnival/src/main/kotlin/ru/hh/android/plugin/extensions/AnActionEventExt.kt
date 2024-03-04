package ru.hh.android.plugin.extensions

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull
import ru.hh.android.plugin.services.git.GitService
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.extensions.getSelectedPsiElement

fun AnActionEvent.canReachKotlinDataClass(): Boolean {
    return when {
        ktClassFromEditor?.isData() == true -> true
        selectedKtClass?.isData() == true -> true
        else -> false
    }
}

fun AnActionEvent.checkInsidePortfolioBranch(): Boolean {
    return portfolioBranchName.isNotBlank()
}

fun AnActionEvent.getCurrentPortfolioBranchName(): String {
    return portfolioBranchName
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
    get() = (getData(PlatformDataKeys.PSI_ELEMENT) as? KtFile)?.classes?.firstIsInstanceOrNull()

private val AnActionEvent.portfolioBranchName: String
    get() = project?.let { GitService.getInstance(it) }?.extractPortfolioBranchName() ?: String.EMPTY
