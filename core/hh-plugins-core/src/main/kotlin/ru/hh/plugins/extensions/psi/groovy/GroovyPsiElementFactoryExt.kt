package ru.hh.plugins.extensions.psi.groovy

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.plugins.model.BuildGradleDependency


fun GroovyPsiElementFactory.createBuildGradleDependencyElement(dependency: BuildGradleDependency): PsiElement {
    val dependencyText = when (dependency) {
        is BuildGradleDependency.MavenArtifact -> "\"${dependency.value}\""
        is BuildGradleDependency.Project -> "project(\"${dependency.value}\")"
        is BuildGradleDependency.LibsConstant -> dependency.value
    }
    val expressionText = "${dependency.configuration.yamlKey} $dependencyText"

    return createExpressionFromText(expressionText)
}