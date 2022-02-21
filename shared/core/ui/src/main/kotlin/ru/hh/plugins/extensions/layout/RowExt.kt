package ru.hh.plugins.extensions.layout

import com.intellij.ui.layout.Row
import ru.hh.plugins.UiConstants.BIG_LABEL_FONT_SIZE
import ru.hh.plugins.extensions.EMPTY
import java.awt.Font
import javax.swing.JLabel

fun Row.titleLabel(text: String = String.EMPTY, fontSize: Float = BIG_LABEL_FONT_SIZE, isBold: Boolean = true) {
    JLabel(text).apply {
        font = font.deriveFont(fontSize)
        if (isBold) {
            font = font.deriveFont(Font.BOLD)
        }
    }()
}

fun Row.boldLabel(text: String = String.EMPTY) {
    JLabel(text).apply {
        font = font.deriveFont(Font.BOLD)
    }()
}
