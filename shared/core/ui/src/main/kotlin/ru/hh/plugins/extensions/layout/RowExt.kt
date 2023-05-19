package ru.hh.plugins.extensions.layout

import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.COLUMNS_SHORT
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.columns
import ru.hh.plugins.extensions.EMPTY
import java.awt.Font
import javax.swing.JLabel
import com.intellij.ui.layout.Row as DslV1Row

/**
 * Backported version of [Row.passwordField] available in IntelliJ Platform since 2023.1
 */
fun Row.passwordFieldCompat(): Cell<JBPasswordField> = cell(JBPasswordField())
    .columns(COLUMNS_SHORT)

fun DslV1Row.boldLabel(text: String = String.EMPTY) {
    JLabel(text).apply {
        font = font.deriveFont(Font.BOLD)
    }()
}
