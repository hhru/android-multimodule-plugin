package ru.hh.plugins.geminio.ui.checkboxlist

interface CheckBoxListViewItem {
    val text: String
    val isForceEnabled: Boolean
    var isChecked: Boolean
}
