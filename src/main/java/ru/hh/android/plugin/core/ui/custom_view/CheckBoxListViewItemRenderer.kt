package ru.hh.android.plugin.core.ui.custom_view

import com.intellij.util.ui.UIUtil
import ru.hh.android.plugin.core.ui.model.CheckBoxListViewItem
import java.awt.Component
import javax.swing.JCheckBox
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.border.EmptyBorder


class CheckBoxListViewItemRenderer<T> : ListCellRenderer<T> where T : CheckBoxListViewItem {

    companion object {
        const val PADDING_VALUE = 10
    }

    private val itemCheckBox = JCheckBox().apply {
        border = EmptyBorder(PADDING_VALUE, PADDING_VALUE, PADDING_VALUE, PADDING_VALUE)
    }


    override fun getListCellRendererComponent(
            list: JList<out T>?,
            value: T,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
    ): Component {
        if (isSelected) {
            itemCheckBox.background = UIUtil.getListSelectionBackground(true)
            itemCheckBox.foreground = UIUtil.getListSelectionForeground(true)
        } else {
            itemCheckBox.background = UIUtil.getListBackground()
            itemCheckBox.foreground = UIUtil.getListForeground()
        }

        itemCheckBox.isEnabled = !value.isForceEnabled
        itemCheckBox.text = value.text
        itemCheckBox.isSelected = value.isForceEnabled || value.isChecked

        return itemCheckBox
    }

}