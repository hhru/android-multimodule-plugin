package ru.hh.android.plugin.wizard.step.choose_applications.model

import com.intellij.openapi.module.Module
import ru.hh.android.plugin.core.ui.model.CheckBoxListViewItem


data class AppModuleDisplayableItem(
        override val text: String,
        override var isChecked: Boolean,
        val gradleModule: Module
) : CheckBoxListViewItem {

    override val isForceEnabled: Boolean = false

}