package ru.hh.android.plugin.extensions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiElement


fun AnActionEvent.getSelectedPsiElement(): PsiElement? = dataContext.getData(PlatformDataKeys.PSI_ELEMENT)