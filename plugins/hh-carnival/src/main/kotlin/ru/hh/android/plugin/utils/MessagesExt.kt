package ru.hh.android.plugin.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.JComponent


fun showErrorMessage(project: Project, errorMessage: String, componentToFocus: JComponent) {
    val errorTitle = PluginBundle.message("geminio.errors.common_title")

    Messages.showMessageDialog(
        project,
        errorMessage,
        errorTitle,
        Messages.getErrorIcon()
    )
    componentToFocus.requestFocusInWindow()
}