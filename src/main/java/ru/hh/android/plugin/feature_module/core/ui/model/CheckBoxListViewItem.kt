package ru.hh.android.plugin.feature_module.core.ui.model


interface CheckBoxListViewItem {
    val text: String
    val isForceEnabled: Boolean
    var isChecked: Boolean
}