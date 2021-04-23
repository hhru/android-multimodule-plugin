package ru.hh.plugins.extensions.psi.kotlin

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtPsiFactory


fun KtPsiFactory.getIncludeModuleExpression(
    moduleName: String,
): PsiElement {
    return createExpression("include(\":$moduleName\")")
}

fun KtPsiFactory.getIncludeModuleRelativePathSetupElement(
    moduleName: String,
    relativePath: String,
): PsiElement {
    return createExpression("project(\":$moduleName\").projectDir = settingsDir.resolve(\"$relativePath\")")
}
