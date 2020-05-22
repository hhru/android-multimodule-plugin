package ru.hh.android.plugin.core.model.enums.extensions

import com.intellij.psi.PsiClass
import com.intellij.psi.util.InheritanceUtil
import ru.hh.android.plugin.core.model.enums.CodeStyleViewDeclaration


/**
 * Simple map for storing mapping of View classes FQNs to code style declarations.
 */
private val codeStyleClassesMap = mutableMapOf<String?, CodeStyleViewDeclaration>()


fun PsiClass.findClosestViewClassDeclaration(): CodeStyleViewDeclaration {
    val mapValue = codeStyleClassesMap[this.qualifiedName]
    if (mapValue != null) {
        return mapValue
    }

    val firstCheckDeclaration = CodeStyleViewDeclaration.values()
        .filter { it != CodeStyleViewDeclaration.VIEW && it != CodeStyleViewDeclaration.TEXT_VIEW }
        .firstOrNull { this.isInheritor(it) }

    return when {
        firstCheckDeclaration != null -> firstCheckDeclaration
        this.isInheritor(CodeStyleViewDeclaration.TEXT_VIEW) -> CodeStyleViewDeclaration.TEXT_VIEW
        else -> CodeStyleViewDeclaration.VIEW
    }.also { codeStyleClassesMap[this.qualifiedName] = it }
}

private fun PsiClass.isInheritor(ViewDeclaration: CodeStyleViewDeclaration): Boolean {
    for (androidViewClassName in ViewDeclaration.androidWidgetsClasses) {
        if (InheritanceUtil.isInheritor(this, androidViewClassName)) {
            return true
        }

    }

    return false
}