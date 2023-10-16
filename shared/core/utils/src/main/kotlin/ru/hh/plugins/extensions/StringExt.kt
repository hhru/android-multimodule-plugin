package ru.hh.plugins.extensions

import com.android.tools.idea.wizard.template.camelCaseToUnderlines
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameHelper
import com.intellij.refactoring.PackageWrapper

private const val KOTLIN_FILE_EXTENSION = ".kt"

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

fun String.toSlashedFilePath(): String {
    return this.replace('.', '/').let { "/$it" }
}

fun String.toKotlinFileName() = "${this}$KOTLIN_FILE_EXTENSION"

fun String.packageToPsiDirectory(project: Project, withPath: String): PsiDirectory? {
    val psiManager = PsiManager.getInstance(project)
    val packageWrapper = PackageWrapper(psiManager, this)

    return packageWrapper.directories.lastOrNull { it.virtualFile.path == withPath }
}

fun String.fromCamelCaseToUnderlines(): String {
    return camelCaseToUnderlines(this)
}

fun String.toUnderlines(): String {
    return this.replace(" ", "").fromCamelCaseToUnderlines()
}

fun String.toPackageNameFromModuleName(packageNamePrefix: String): String {
    val formattedModuleName = this
        .replace(Char.SPACE, Char.UNDERSCORE)
        .replace(Char.HYPHEN, Char.UNDERSCORE)
    return "$packageNamePrefix.$formattedModuleName"
}

fun String.replaceWordsBreakers(): String {
    return this.replace('-', '_')
        .replace('_', ' ')
        .replace("  ", " ")
}

fun String.toFormattedModuleName(): String {
    val moduleName = this

    return with(StringBuilder()) {
        moduleName
            .replace("_api", "")
            .replace("_impl", "")
            .replace("feature_","")
            .replaceWordsBreakers()
            .split(' ')
            .map { it.capitalize() }
            .forEach { append(it) }
        toString()
    }
}
