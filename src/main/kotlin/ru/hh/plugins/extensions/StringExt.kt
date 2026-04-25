package ru.hh.plugins.extensions

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameHelper

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

fun String.toSlashedFilePath(): String {
    return this.replace('.', '/').let { "/$it" }
}

fun String.toFilePathFromGradleModulePath(): String {
    return trim(':').replace(':', Char.SLASH)
}

fun String.fromCamelCaseToUnderlines(): String {
    if (isEmpty()) {
        return String.EMPTY
    }

    val normalized = replace('-', '_').replace(' ', '_')
    val builder = StringBuilder()

    normalized.forEachIndexed { index, char ->
        when {
            char == '_' -> {
                if (builder.isNotEmpty() && builder.last() != '_') {
                    builder.append('_')
                }
            }

            char.isUpperCase() -> {
                if (index != 0 && builder.isNotEmpty() && builder.last() != '_') {
                    builder.append('_')
                }
                builder.append(char.lowercaseChar())
            }

            else -> builder.append(char.lowercaseChar())
        }
    }

    return builder.toString().trim('_')
}

fun String.toUnderlines(): String {
    return this.replace(" ", "").fromCamelCaseToUnderlines()
}

fun String.toPackageNameFromModuleName(packageNamePrefix: String): String {
    val formattedModuleName = this
        .replace(':', Char.DOT)
        .trim(Char.DOT)
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
            .replaceWordsBreakers()
            .split(' ')
            .map { it.capitalizeAscii() }
            .forEach { append(it) }
        toString()
    }
}

private fun String.capitalizeAscii(): String {
    return replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.uppercaseChar().toString()
        } else {
            char.toString()
        }
    }
}
