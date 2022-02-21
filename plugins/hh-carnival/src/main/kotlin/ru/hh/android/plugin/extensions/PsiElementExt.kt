package ru.hh.android.plugin.extensions

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMember

fun PsiElement.firstChildWithStartText(startText: String): PsiElement? {
    return children.firstOrNull { it.text.startsWith(startText) }
}

fun PsiMember.findAnnotationWithName(annotationName: String): PsiElement? {
    return annotations.firstOrNull { it.text.contains(annotationName) }
}
