package ru.hh.plugins.geminio.sdk.extensions.psi.groovy

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.plugins.geminio.sdk.recipe.models.commands.BuildGradleDependency


internal fun GroovyPsiElementFactory.createBuildGradleDependencyElement(
    dependency: BuildGradleDependency
): PsiElement {
    val dependencyText = when (dependency) {
        is BuildGradleDependency.MavenArtifact -> "\"${dependency.value}\""
        is BuildGradleDependency.Project -> "project(\"${dependency.value}\")"
        is BuildGradleDependency.LibsConstant -> dependency.value
    }
    val expressionText = "${dependency.configuration.yamlKey} $dependencyText"

    return createExpressionFromText(expressionText)
}