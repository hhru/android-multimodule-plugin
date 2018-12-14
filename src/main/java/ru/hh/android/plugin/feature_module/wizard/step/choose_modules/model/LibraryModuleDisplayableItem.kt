package ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model

import ru.hh.android.plugin.feature_module.core.ui.model.CheckBoxListViewItem


data class LibraryModuleDisplayableItem(
        override val text: String,
        override val isForceEnabled: Boolean,
        override var isChecked: Boolean
) : CheckBoxListViewItem