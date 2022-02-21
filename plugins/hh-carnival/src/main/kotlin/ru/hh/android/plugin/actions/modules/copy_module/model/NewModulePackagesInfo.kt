package ru.hh.android.plugin.actions.modules.copy_module.model

import com.intellij.psi.PsiDirectory

data class NewModulePackagesInfo(
    val moduleToCopyPackageName: String,
    val moduleToCopyMainPackagePsiDirectory: PsiDirectory,
    val moduleToCopyMainSourceSetPsiDirectory: PsiDirectory,
    val newModulePackageName: String,
    val newModuleMainPackagePsiDirectory: PsiDirectory,
    val newModuleMainSourceSetPsiDirectory: PsiDirectory
)
