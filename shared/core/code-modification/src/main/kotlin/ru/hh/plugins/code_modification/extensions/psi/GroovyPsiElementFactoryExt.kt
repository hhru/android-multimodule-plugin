package ru.hh.plugins.code_modification.extensions.psi

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory


private const val GR_TOKEN_LINE_BREAK_SYMBOL = "\n"


fun GroovyPsiElementFactory.getBreakLineElement(): PsiElement {
    return createLineTerminator(GR_TOKEN_LINE_BREAK_SYMBOL)
}

fun GroovyPsiElementFactory.getIncludeModuleExpressionElement(moduleName: String): PsiElement {
    return createExpressionFromText("include ':$moduleName'")
}

fun GroovyPsiElementFactory.getIncludeModuleRelativePathSetupElement(
    moduleName: String,
    relativePath: String
): PsiElement {
    return createExpressionFromText(
        "project(':$moduleName').projectDir = new File(settingsDir, \"$relativePath\")"
    )
}