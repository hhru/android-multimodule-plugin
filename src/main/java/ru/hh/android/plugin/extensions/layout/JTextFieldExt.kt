package ru.hh.android.plugin.extensions.layout

import com.intellij.ui.DocumentAdapter
import javax.swing.JTextField
import javax.swing.event.DocumentEvent


inline fun JTextField.onTextChange(crossinline action: (DocumentEvent) -> Unit) {
    document.addDocumentListener(object : DocumentAdapter() {
        override fun textChanged(e: DocumentEvent) {
            action.invoke(e)
        }
    })
}