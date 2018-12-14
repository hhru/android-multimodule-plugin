package ru.hh.android.plugin.feature_module.core.ui.custom_view

import com.intellij.util.ui.UIUtil
import ru.hh.android.plugin.feature_module.core.ui.model.CheckBoxListViewItem
import java.awt.Component
import javax.swing.JCheckBox
import javax.swing.JList
import javax.swing.ListCellRenderer


class CheckBoxListViewItemRenderer<T> : ListCellRenderer<T> where T : CheckBoxListViewItem {

    private val itemCheckBox = JCheckBox()


    override fun getListCellRendererComponent(
            list: JList<out T>?,
            value: T,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
    ): Component {
        if (isSelected) {
            itemCheckBox.background = UIUtil.getListSelectionBackground()
            itemCheckBox.foreground = UIUtil.getListSelectionForeground()
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