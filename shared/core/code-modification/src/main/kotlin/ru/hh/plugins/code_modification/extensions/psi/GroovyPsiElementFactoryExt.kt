package ru.hh.plugins.code_modification.extensions.psi

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.plugins.code_modification.models.BuildGradleDependency
import ru.hh.plugins.code_modification.models.extensions.toDependencyText


private const val GR_TOKEN_LINE_BREAK_SYMBOL = "\n"


fun GroovyPsiElementFactory.createNewLine(): PsiElement {
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

fun GroovyPsiElementFactory.createBuildGradleDependencyElement(
    dependency: BuildGradleDependency
): PsiElement {
    val dependencyText = dependency.toDependencyText()
    val expressionText = "${dependency.configuration.yamlKey} $dependencyText"

    return createExpressionFromText(expressionText)
}