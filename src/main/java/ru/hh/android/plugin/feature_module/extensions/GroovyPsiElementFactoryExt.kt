package ru.hh.android.plugin.feature_module.extensions

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression

private const val LINE_BREAK_SYMBOL = "\n"


fun GroovyPsiElementFactory.createBreakLineElement(): PsiElement {
    return createLineTerminator(LINE_BREAK_SYMBOL)
}

fun GroovyPsiElementFactory.generateIncludeExpression(moduleName: String): GrExpression {
    return createExpressionFromText("include ':$moduleName'")
}

fun GroovyPsiElementFactory.createProjectDirPathExpression(
        moduleName: String,
        folderPath: String
): GrExpression {
    return createExpressionFromText(
            "project(':$moduleName').projectDir = new File(settingsDir, '$folderPath')")
}

fun GroovyPsiElementFactory.createModuleDependencyExpression(moduleName: String): GrExpression {
    return createExpressionFromText("implementation project(':$moduleName')")
}