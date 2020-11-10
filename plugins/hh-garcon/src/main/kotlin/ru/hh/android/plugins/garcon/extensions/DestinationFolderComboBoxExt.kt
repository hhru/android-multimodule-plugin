package ru.hh.android.plugins.garcon.extensions

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.refactoring.PackageWrapper
import com.intellij.refactoring.move.moveClassesOrPackages.AutocreatingSingleSourceRootMoveDestination
import com.intellij.refactoring.move.moveClassesOrPackages.DestinationFolderComboBox


fun DestinationFolderComboBox.getTargetFilePath(project: Project, targetPackageName: String): String? {
    val psiManager = PsiManager.getInstance(project)
    val targetPackageWrapper = PackageWrapper(psiManager, targetPackageName)

    val moveDestination = (this.selectDirectory(targetPackageWrapper, false))
            as? AutocreatingSingleSourceRootMoveDestination

    val field = AutocreatingSingleSourceRootMoveDestination::class.java.getDeclaredField("mySourceRoot")
        .also { it.isAccessible = true }

    return moveDestination?.let { field.get(it) as? VirtualFile? }?.path
}