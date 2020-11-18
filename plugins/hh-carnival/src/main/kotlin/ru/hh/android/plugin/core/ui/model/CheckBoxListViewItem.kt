package ru.hh.android.plugin.core.ui.model


interface CheckBoxListViewItem {
    val text: String
    val isForceEnabled: Boolean
    var isChecked: Boolean
}