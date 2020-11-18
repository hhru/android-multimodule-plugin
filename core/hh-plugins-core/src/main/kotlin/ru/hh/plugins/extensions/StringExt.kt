package ru.hh.plugins.extensions

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameHelper
import com.intellij.refactoring.PackageWrapper
import ru.hh.plugins.PluginsConstants


val String.Companion.EMPTY: String get() = ""

fun String.isQualifiedPackageName(project: Project): Boolean {
    val psiManager = PsiManager.getInstance(project)
    val nameHelper = PsiNameHelper.getInstance(psiManager.project)

    return nameHelper.isQualifiedName(this)
}

fun String.isValidIdentifier(project: Project): Boolean {
    val psiManager = PsiManager.getInstance(project)
    val nameHelper = PsiNameHelper.getInstance(psiManager.project)

    return nameHelper.isIdentifier(this)
}

fun String.replaceLineBreaks(): String {
    return this.replace("\n", "\n<br />", true)
}

fun String.toKotlinFileName() = "${this}${PluginsConstants.KOTLIN_FILE_EXTENSION}"


fun String.packageToPsiDirectory(project: Project, withPath: String): PsiDirectory? {
    val psiManager = PsiManager.getInstance(project)
    val packageWrapper = PackageWrapper(psiManager, this)

    return packageWrapper.directories.lastOrNull { it.virtualFile.path == withPath }
}
