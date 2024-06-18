package ru.hh.plugins.extensions.layout

import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.Cell
import javax.swing.JComponent
import javax.swing.JList

/**
 * Backported version of [Cell.enabledIf(ObservableProperty<Boolean)][Cell.enabledIf] available
 * in IntelliJ Platform since 2023.1
 */
fun <C : JComponent> Cell<C>.enabledIfCompat(property: ObservableProperty<Boolean>): Cell<C> = apply {
    enabled(property.get())
    property.afterChange {
        enabled(it)
    }
    return this
}

/**
 * Backported version of [Cell.visibleIf(ObservableProperty<Boolean)][Cell.enabledIf] available
 * in IntelliJ Platform since 2023.1
 */
fun <C : JComponent> Cell<C>.visibleIfCompat(property: ObservableProperty<Boolean>): Cell<C> = apply {
    visible(property.get())
    property.afterChange {
        visible(it)
    }
}

/**
 * Backported version of [com.intellij.ui.dsl.builder.UtilsKt.listCellRenderer] available
 * in IntelliJ Platform since 2023.1
 */
fun <T> listCellRenderer(renderer: SimpleListCellRenderer<T>.(T) -> Unit): SimpleListCellRenderer<T> {
    return object : SimpleListCellRenderer<T>() {
        override fun customize(list: JList<out T>, value: T, index: Int, selected: Boolean, hasFocus: Boolean) {
            // BasicComboBoxUI.getBaseline can try to get renderer for null value even when comboBox doesn't allow
            // nullable elements
            if (index != -1 || value != null) {
                renderer(value)
            }
        }
    }
}
