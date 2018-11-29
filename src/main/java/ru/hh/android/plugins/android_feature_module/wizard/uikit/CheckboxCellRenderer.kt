package ru.hh.android.plugins.android_feature_module.wizard.uikit

import com.intellij.util.ui.UIUtil
import ru.hh.android.plugins.android_feature_module.models.ui.CheckBoxItem
import java.awt.Component
import javax.swing.JCheckBox
import javax.swing.JList
import javax.swing.ListCellRenderer


class CheckboxCellRenderer<T : CheckBoxItem>(
        private val forceEnabledItems: List<String>
) : ListCellRenderer<T> {

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

        itemCheckBox.isEnabled = !forceEnabledItems.contains(value.text)
        itemCheckBox.text = value.text
        itemCheckBox.isSelected = forceEnabledItems.contains(value.text) || value.isEnabled

        return itemCheckBox
    }

}