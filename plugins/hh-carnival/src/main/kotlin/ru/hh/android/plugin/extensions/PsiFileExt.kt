package ru.hh.android.plugin.extensions

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory


inline fun PsiFile.copyFile(textModification: (String) -> String = { it }): PsiFile {
    val psiFileFactory = PsiFileFactory.getInstance(project)
    return psiFileFactory.createFileFromText(name, language, textModification.invoke(text))
}