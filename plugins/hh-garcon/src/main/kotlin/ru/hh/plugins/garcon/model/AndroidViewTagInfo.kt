package ru.hh.plugins.garcon.model

import com.intellij.psi.PsiClass
import com.intellij.psi.xml.XmlFile

data class AndroidViewTagInfo(
    val id: String,
    val xmlFile: XmlFile,
    val tagPsiClass: PsiClass
)
