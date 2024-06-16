package ru.hh.plugins.garcon.live_templates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

class LiveTemplateItemContext : TemplateContextType(LIVE_TEMPLATE_ITEM_NAME) {

    private companion object {
        const val LIVE_TEMPLATE_ITEM_NAME = "KRecyclerView ItemType context"

        const val KAKAO_RECYCLER_VIEW_CLASS_NAME = "KRecyclerView"
    }

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val psiElement = templateActionContext.file.findElementAt(templateActionContext.startOffset)

        return PsiTreeUtil.getParentOfType(psiElement, KtCallExpression::class.java)
            ?.calleeExpression
            ?.references
            ?.firstIsInstanceOrNull<KtSimpleNameReference>()
            ?.value == KAKAO_RECYCLER_VIEW_CLASS_NAME
    }

}
