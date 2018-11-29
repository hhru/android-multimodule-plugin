package ru.hh.android.plugins.android_feature_module.wizard.uikit

import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.ClickListener
import com.intellij.ui.CollectionListModel
import ru.hh.android.plugins.android_feature_module.models.ui.CheckBoxItem
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JList

class CheckboxesListView<T : CheckBoxItem>(
        private val onItemSelectedListener: ((T?) -> Unit)? = null,
        private val onItemToggleChangedListener: ((T) -> Unit)? = null
) : JList<T>() {

    private lateinit var items: List<T>
    private lateinit var forceEnabledItems: List<String>


    init {
        addListSelectionListener { onItemSelectedListener?.invoke(getSelectedItem()) }

        val clickableArea = JCheckBox("").minimumSize.width
        (object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                if (event.x < clickableArea) {
                    toggleSelection()
                }

                return true
            }
        }).installOn(this)

        addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent) {
                if (e.keyChar == ' ') {
                    toggleSelection()
                }
            }
        })
    }

    fun setItems(items: List<T>, forceEnabledItems: List<String>) {
        this.items = items.sortedWith(Comparator { o1, o2 ->
            StringUtil.compare(o1.text, o2.text, true)
        })
        this.forceEnabledItems = forceEnabledItems

        cellRenderer = CheckboxCellRenderer<T>(forceEnabledItems)

        model = CollectionListModel(this.items)
        selectedIndex = 0
    }


    private fun toggleSelection() {
        val currentItem = getSelectedItem() ?: return
        val isSelectedItemEnabled = currentItem.isEnabled

        for (selectedItem in selectedValuesList) {
            selectedItem.isEnabled = !isSelectedItemEnabled
            onItemToggleChangedListener?.invoke(selectedItem)
        }

        repaint()
    }

    private fun getSelectedItem(): T? {
        val leadSelectionIndex = selectionModel.leadSelectionIndex
        return if (leadSelectionIndex < 0) null else items[leadSelectionIndex]
    }

}