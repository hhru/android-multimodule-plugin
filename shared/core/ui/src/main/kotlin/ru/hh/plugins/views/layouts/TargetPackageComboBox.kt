package ru.hh.plugins.views.layouts

import com.intellij.openapi.project.Project
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo
import com.intellij.ui.dsl.builder.Row

private const val MINIMUM_GAP_FOR_COMBO_BOX_IN_PX = 5
private const val MIN_COMBO_BOX_WIDTH_IN_PX = 40

/**
 * Creates [com.intellij.refactoring.ui.PackageNameReferenceEditorCombo] with default package name.
 */
fun Row.targetPackageComboBox(
    project: Project,
    initialText: String,
    recentPackageKey: String,
    labelText: String
): PackageNameReferenceEditorCombo {
    return PackageNameReferenceEditorCombo(
        initialText,
        project,
        recentPackageKey,
        labelText
    ).apply {
        val preferredWidth = (initialText.length + MINIMUM_GAP_FOR_COMBO_BOX_IN_PX)
            .coerceAtLeast(MIN_COMBO_BOX_WIDTH_IN_PX)
        setTextFieldPreferredWidth(preferredWidth)
    }
}
