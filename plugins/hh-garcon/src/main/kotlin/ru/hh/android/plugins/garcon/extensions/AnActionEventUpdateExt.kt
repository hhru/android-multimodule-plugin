package ru.hh.android.plugins.garcon.extensions

import com.android.tools.idea.util.androidFacet
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.util.InheritanceUtil
import org.jetbrains.kotlin.asJava.toLightClass


private const val LAYOUT_RES_DIRECTORY_NAME = "layout"


fun AnActionEvent.canReachLayoutXmlFileInsideAndroidModule(): Boolean {
    val xmlPsiFile = this.getXmlFileFromEditorOrSelection()

    return xmlPsiFile != null
            && xmlPsiFile.parent?.name?.startsWith(LAYOUT_RES_DIRECTORY_NAME) == true
            && xmlPsiFile.androidFacet != null
}

fun AnActionEvent.canReachKtClassInsideAndroidModuleWithAncestor(ancestorClassFQN: String): Boolean {
    val ktClass = this.getKtClassFromEditor()
    val kotlinPsiClass = ktClass?.toLightClass()
    val hasRightAncestor = InheritanceUtil.isInheritor(kotlinPsiClass, ancestorClassFQN)

    return ktClass != null
            && hasRightAncestor
            && ktClass.androidFacet != null
}