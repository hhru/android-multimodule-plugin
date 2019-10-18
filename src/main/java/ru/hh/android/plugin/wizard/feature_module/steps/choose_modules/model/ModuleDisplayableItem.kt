package ru.hh.android.plugin.wizard.feature_module.steps.choose_modules.model

import ru.hh.android.plugin.core.ui.model.CheckBoxListViewItem


data class ModuleDisplayableItem(
        override val text: String,
        override val isForceEnabled: Boolean,
        override var isChecked: Boolean
) : CheckBoxListViewItem