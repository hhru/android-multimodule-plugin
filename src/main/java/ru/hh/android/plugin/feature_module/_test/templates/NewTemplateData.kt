package ru.hh.android.plugin.feature_module._test.templates

import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.PsiDirectory


data class NewTemplateData(
        val templateName: String,
        val targetFileName: String,
        val fileType: FileType,
        val targetPsiDirectory: PsiDirectory?
)