package ru.hh.plugins.psi_utils

import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager

fun PsiElement.reformatWithCodeStyle() {
    CodeStyleManager.getInstance(project).reformat(this)
}
