package ru.hh.plugins.psi_utils

import com.intellij.psi.PsiClass
import com.intellij.psi.util.InheritanceUtil

fun PsiClass.isInheritedFrom(fullyQualifiedClassName: String): Boolean {
    return InheritanceUtil.isInheritor(this, fullyQualifiedClassName)
}
