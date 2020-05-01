package ru.hh.android.plugin.actions.modules.copy_module.model

import com.intellij.psi.PsiDirectory


data class NewModuleDirectoriesStructure(
    val moduleToCopyMainSourceSetPsiDirectory: PsiDirectory,
    val moduleToCopyJavaSourcePsiDirectory: PsiDirectory,
    val newModuleMainSourceSetPsiDirectory: PsiDirectory,
    val newModuleJavaSourcePsiDirectory: PsiDirectory
)