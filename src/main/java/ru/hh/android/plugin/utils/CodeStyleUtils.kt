package ru.hh.android.plugin.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager


fun PsiElement.reformatWithCodeStyle() {
    CodeStyleManager.getInstance(this.project).reformat(this)
}