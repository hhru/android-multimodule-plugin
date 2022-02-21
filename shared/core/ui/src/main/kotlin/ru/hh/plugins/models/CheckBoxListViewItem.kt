package ru.hh.plugins.models

interface CheckBoxListViewItem {
    val text: String
    val isForceEnabled: Boolean
    var isChecked: Boolean
}
