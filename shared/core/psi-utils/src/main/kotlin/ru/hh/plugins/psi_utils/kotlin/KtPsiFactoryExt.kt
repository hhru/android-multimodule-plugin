package ru.hh.plugins.psi_utils.kotlin

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath
import ru.hh.plugins.models.gradle.BuildGradleDependency
import ru.hh.plugins.models.gradle.extensions.toDependencyText

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

fun KtPsiFactory.createBuildGradleDependencyElement(
    buildGradleDependency: BuildGradleDependency
): PsiElement {
    val dependencyText = buildGradleDependency.toDependencyText()

    return createExpression("${buildGradleDependency.configuration.yamlKey}($dependencyText)")
}

fun KtPsiFactory.createImport(
    importPath: String
): PsiElement {
    return createImportDirective(ImportPath.fromString(importPath))
}

fun KtPsiFactory.createModuleField(moduleName:String): PsiElement {
    return createExpression("$moduleName.module")
}

fun KtPsiFactory.createGradlePluginElement(pluginId: String): PsiElement {
    return createExpression("id(\"${pluginId}\")")
}
