package ru.hh.plugins.garcon.live_templates

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

class LiveTemplateItemContext : TemplateContextType(LIVE_TEMPLATE_ITEM_ID, LIVE_TEMPLATE_ITEM_NAME) {

    companion object {
        private const val LIVE_TEMPLATE_ITEM_ID = "ru.hh.android.plugins.garcon.live_templates.item"
        private const val LIVE_TEMPLATE_ITEM_NAME = "KRecyclerView ItemType context"

        private const val KAKAO_RECYCLER_VIEW_CLASS_NAME = "KRecyclerView"
    }

    override fun isInContext(file: PsiFile, offset: Int): Boolean {
        val psiElement = file.findElementAt(offset)

        return PsiTreeUtil.getParentOfType(psiElement, KtCallExpression::class.java)
            ?.calleeExpression
            ?.references
            ?.firstIsInstanceOrNull<KtSimpleNameReference>()
            ?.value == KAKAO_RECYCLER_VIEW_CLASS_NAME
    }
}
