package ru.hh.android.plugins.garcon.model

import com.intellij.psi.PsiClass


data class AndroidViewTagInfo(
    val id: String,
    val xmlFieName: String,
    val tagPsiClass: PsiClass
)