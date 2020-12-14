package ru.hh.android.plugin.extensions.psi.groovy

import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import ru.hh.android.plugin.core.model.psi.GradleDependency
import ru.hh.android.plugin.core.model.psi.GradleDependencyMode
import ru.hh.android.plugin.core.model.psi.GradleDependencyType


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