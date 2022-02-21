package ru.hh.plugins.garcon.live_templates

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile
import com.intellij.psi.util.InheritanceUtil
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.psi.KtClass
import ru.hh.plugins.garcon.GarconConstants

class LiveTemplateStepContext : TemplateContextType(LIVE_TEMPLATE_STEP_ID, LIVE_TEMPLATE_STEP_NAME) {

    companion object {
        private const val LIVE_TEMPLATE_STEP_ID = "ru.hh.android.plugins.garcon.live_templates.step"
        private const val LIVE_TEMPLATE_STEP_NAME = "Step function context"
    }

    override fun isInContext(file: PsiFile, offset: Int): Boolean {
        val psiElement = file.findElementAt(offset)
        val closestKtClass = PsiTreeUtil.getParentOfType(psiElement, KtClass::class.java)?.toLightClass()
            ?: return false

        return InheritanceUtil.isInheritor(closestKtClass, GarconConstants.HH_SCREEN_INTENTIONS_CLASS_FQN) ||
            InheritanceUtil.isInheritor(closestKtClass, GarconConstants.AGODA_SCREEN_CLASS_FQN)
    }
}
