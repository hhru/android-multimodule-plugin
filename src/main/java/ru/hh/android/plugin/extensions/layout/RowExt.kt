package ru.hh.android.plugin.extensions.layout

import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.Row
import ru.hh.android.plugin.extensions.EMPTY
import java.awt.Font
import javax.swing.JLabel

private const val BIG_LABEL_FONT_SIZE = 18.0f

fun LayoutBuilder.bigTitleRow(text: String = String.EMPTY, fontSize: Float = BIG_LABEL_FONT_SIZE, isBold: Boolean = true): Row {
    return row {
        titleLabel(text, fontSize, isBold)
    }
}

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