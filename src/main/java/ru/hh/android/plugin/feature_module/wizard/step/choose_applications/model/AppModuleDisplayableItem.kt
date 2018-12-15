package ru.hh.android.plugin.feature_module.wizard.step.choose_applications.model

import com.intellij.openapi.module.Module
import ru.hh.android.plugin.feature_module.core.ui.model.CheckBoxListViewItem


data class AppModuleDisplayableItem(
        override val text: String,
        override var isChecked: Boolean,
        val gradleModule: Module
) : CheckBoxListViewItem {

    override val isForceEnabled: Boolean = false

}