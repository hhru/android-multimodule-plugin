package ru.hh.plugins.views.layouts

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo
import com.intellij.ui.dsl.builder.Row
import org.jetbrains.kotlin.idea.refactoring.ui.KotlinDestinationFolderComboBox

fun Row.kotlinDestinationFolderComboBox(
    project: Project,
    initialPsiDirectory: PsiDirectory,
    packageNameChooserComboBox: PackageNameReferenceEditorCombo
): KotlinDestinationFolderComboBox {
    val destinationFolderComboBox = object : KotlinDestinationFolderComboBox() {
        override fun getTargetPackage(): String {
            return packageNameChooserComboBox.text.trim()
        }
    }

    destinationFolderComboBox.setData(
        project,
        initialPsiDirectory,
        packageNameChooserComboBox.childComponent
    )

    return destinationFolderComboBox
}
