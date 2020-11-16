package ru.hh.plugins.extensions.psi

import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory
import com.intellij.util.IncorrectOperationException


fun PsiDirectory.checkFileCanBeCreated(fileName: String): Boolean {
    return try {
        checkCreateFile(fileName)
        true
    } catch (ex: IncorrectOperationException) {
        false
    }
}

val PsiDirectory.qualifiedPackageName: String?
    get() {
        val aPackage = JavaDirectoryService.getInstance().getPackage(this)
        return aPackage?.qualifiedName
    }