package ru.hh.android.plugin.wizard.step.choose_modules.model

import ru.hh.android.plugin.core.ui.model.CheckBoxListViewItem


data class LibraryModuleDisplayableItem(
        override val text: String,
        override val isForceEnabled: Boolean,
        override var isChecked: Boolean
) : CheckBoxListViewItem