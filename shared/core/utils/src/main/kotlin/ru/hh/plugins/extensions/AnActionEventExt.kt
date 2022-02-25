package ru.hh.plugins.extensions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiElement

fun AnActionEvent.getSelectedPsiElement(): PsiElement? = getData(PlatformDataKeys.PSI_ELEMENT)
