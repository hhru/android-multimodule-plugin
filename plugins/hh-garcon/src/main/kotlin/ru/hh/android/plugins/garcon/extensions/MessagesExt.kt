package ru.hh.android.plugins.garcon.extensions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import ru.hh.android.plugins.garcon.utils.GarconBundle
import javax.swing.JComponent


fun showErrorMessage(project: Project, errorMessage: String, componentToFocus: JComponent) {
    val errorTitle = GarconBundle.message("garcon.errors.common_title")

    Messages.showMessageDialog(
        project,
        errorMessage,
        errorTitle,
        Messages.getErrorIcon()
    )
    componentToFocus.requestFocusInWindow()
}