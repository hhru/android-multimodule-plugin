package ru.hh.android.plugin.extensions.psi.groovy

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import ru.hh.android.plugin.core.model.psi.GradleDependency
import ru.hh.android.plugin.core.model.psi.GradleDependencyMode
import ru.hh.android.plugin.core.model.psi.GradleDependencyType

private const val GR_TOKEN_LINE_BREAK_SYMBOL = "\n"


fun GroovyPsiElementFactory.generateIncludeExpression(moduleName: String): GrExpression {
    return createExpressionFromText("include ':$moduleName'")
}

fun GroovyPsiElementFactory.createProjectDirPathExpression(
    moduleName: String,
    folderPath: String
): GrExpression {
    return createExpressionFromText(
        """project(':$moduleName').projectDir = new File(settingsDir, "$folderPath")""")
}


fun GroovyPsiElementFactory.getGradleDependencyExpression(dependency: GradleDependency): GrExpression {
    val startToken = when (dependency.mode) {
        GradleDependencyMode.IMPLEMENTATION -> "implementation"
        GradleDependencyMode.COMPILE_ONLY -> "compileOnly"
        GradleDependencyMode.KAPT -> "kapt"
    }
    val endToken = when (dependency.type) {
        GradleDependencyType.MODULE -> " project(':${dependency.text}')"
        GradleDependencyType.LIBRARY_CONSTANT -> " ${dependency.text}"
    }

    val expressionText = "$startToken$endToken"
    return createExpressionFromText(expressionText)
}

fun GroovyPsiElementFactory.getBreakLineElement(): PsiElement {
    return createLineTerminator(GR_TOKEN_LINE_BREAK_SYMBOL)
}

fun GroovyPsiElementFactory.getIncludeModuleExpressionElement(moduleName: String): PsiElement {
    return createExpressionFromText("include ':$moduleName'")
}

fun GroovyPsiElementFactory.getIncludeModuleRelativePathSetupElement(moduleName: String, relativePath: String): PsiElement {
    return createExpressionFromText("project(':$moduleName').projectDir = new File(settingsDir, \"$relativePath\")")
}