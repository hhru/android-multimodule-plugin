package ru.hh.plugins.extensions.layout

import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.Row
import ru.hh.plugins.UiConstants.BIG_LABEL_FONT_SIZE
import ru.hh.plugins.extensions.EMPTY


fun LayoutBuilder.bigTitleRow(
    text: String = String.EMPTY,
    fontSize: Float = BIG_LABEL_FONT_SIZE,
    isBold: Boolean = true
): Row {
    return row {
        titleLabel(text, fontSize, isBold)
    }
}