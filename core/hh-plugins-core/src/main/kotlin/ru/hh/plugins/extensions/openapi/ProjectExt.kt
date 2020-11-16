package ru.hh.plugins.extensions.openapi

import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleManager


/**
 * You can use this method for adding code without applying code style.
 * When you generate code with fully qualified class names your lines can be much more longer than max line length
 * defined in your code style.
 *
 * [com.intellij.psi.PsiElement.addAfter] or [com.intellij.psi.PsiElement.addBefore] by default adds
 * new element with code style applying -> some lines can be added with wrong indents and line breaks.
 */
fun Project.executeWithoutCodeStyle(action: () -> Unit) {
    CodeStyleManager.getInstance(this).performActionWithFormatterDisabled(action)
}