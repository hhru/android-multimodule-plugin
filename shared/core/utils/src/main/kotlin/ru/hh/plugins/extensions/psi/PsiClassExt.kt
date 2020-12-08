package ru.hh.plugins.extensions.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.util.InheritanceUtil


fun PsiClass.isInheritedFrom(fullyQualifiedClassName: String): Boolean {
    return InheritanceUtil.isInheritor(this, fullyQualifiedClassName)
}