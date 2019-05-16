package ru.hh.android.plugin.config

import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.JTextField


class PluginConfigEditor(
        private val defaultPluginFolderName: String
) {

    companion object {
        private const val CHOOSE_PATH_FOLDER_DIR_APPROVE_BUTTON_TEXT = "Save"
    }


    private lateinit var rootPanel: JPanel
    private lateinit var pathToPluginTextField: JTextField
    private lateinit var choosePluginFolderButton: JButton


    fun getRootPanel(): JPanel {
        return rootPanel
    }

    fun getPathToPluginFolder(): String {
        return pathToPluginTextField.text
    }


    private fun createUIComponents() {
        pathToPluginTextField = JTextField()
        pathToPluginTextField.text = defaultPluginFolderName

        choosePluginFolderButton = JButton()
        choosePluginFolderButton.addActionListener { choosePluginFolderDir() }
    }


    private fun choosePluginFolderDir() {
        val fileChooser = JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        }

        val result = fileChooser.showDialog(rootPanel, CHOOSE_PATH_FOLDER_DIR_APPROVE_BUTTON_TEXT)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            pathToPluginTextField.text = selectedFile.absolutePath
        }
    }

}
