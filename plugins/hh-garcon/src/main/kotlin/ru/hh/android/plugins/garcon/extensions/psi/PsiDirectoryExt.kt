package ru.hh.android.plugins.garcon.extensions.psi

import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory


val PsiDirectory.qualifiedPackageName: String?
    get() {
        val aPackage = JavaDirectoryService.getInstance().getPackage(this)
        return aPackage?.qualifiedName
    }