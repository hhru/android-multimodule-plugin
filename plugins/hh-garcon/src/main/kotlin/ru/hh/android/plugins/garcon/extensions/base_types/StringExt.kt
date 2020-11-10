package ru.hh.android.plugins.garcon.extensions.base_types

import com.android.tools.idea.templates.TemplateUtils
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameHelper
import com.intellij.refactoring.PackageWrapper


val String.Companion.EMPTY get() = ""

fun String.snakeToCamelCase(): String {
    return TemplateUtils.underlinesToCamelCase(this)
}

fun String.layoutFileNameToClassName(): String {
    return removeSuffix(".xml").snakeToCamelCase()
}

fun String.getClassNameFromFQN() = this.split(".").last()

fun String.isValidIdentifier(project: Project): Boolean {
    val psiManager = PsiManager.getInstance(project)
    val nameHelper = PsiNameHelper.getInstance(psiManager.project)

    return nameHelper.isIdentifier(this)
}

fun String.isQualifiedPackageName(project: Project): Boolean {
    val psiManager = PsiManager.getInstance(project)
    val nameHelper = PsiNameHelper.getInstance(psiManager.project)

    return nameHelper.isQualifiedName(this)
}

fun String.packageToPsiDirectory(project: Project, withPath: String): PsiDirectory? {
    val psiManager = PsiManager.getInstance(project)
    val packageWrapper = PackageWrapper(psiManager, this)

    return packageWrapper.directories.lastOrNull { it.virtualFile.path == withPath }
}

fun String.replaceLineBreaks(): String {
    return this.replace("\n", "\n<br />", true)
}