package ru.hh.android.plugin.feature_module.extensions

import com.intellij.psi.PsiElement


fun PsiElement.firstChildWithStartText(startText: String): PsiElement? {
    return children.firstOrNull { it.text.startsWith(startText) }
}