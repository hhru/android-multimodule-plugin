package ru.hh.plugins.views.layouts

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.Row
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


/**
 * Build button for files choosing.
 *
 * @param project -- current project
 * @param buttonText -- text on button that will show dialog
 * @param filterText -- text in your file chooser dialog
 * @param fileChooserButtonText -- text on approve button in dialog
 * @param filterFilesExtensions -- extensions for filtering your files (without ".", e.g. "yaml")
 * @param approveAction -- action that will be invoked, if user choose something
 */
fun Row.fileChooserButton(
    project: Project,
    buttonText: String,
    filterText: String,
    fileChooserButtonText: String,
    vararg filterFilesExtensions: String,
    approveAction: (File) -> Unit
) {
    button(buttonText) {
        val fileChooser = JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            fileFilter = FileNameExtensionFilter(filterText, *filterFilesExtensions)
            project.basePath?.let { currentDirectory = File(it) }
        }

        val result = fileChooser.showDialog(null, fileChooserButtonText)
        if (result == JFileChooser.APPROVE_OPTION) {
            approveAction.invoke(fileChooser.selectedFile)
        }
    }
}