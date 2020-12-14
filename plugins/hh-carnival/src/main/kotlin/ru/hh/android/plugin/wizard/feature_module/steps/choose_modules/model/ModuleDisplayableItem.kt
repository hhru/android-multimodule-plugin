package ru.hh.android.plugin.wizard.feature_module.steps.choose_modules.model

import com.intellij.openapi.module.Module
import ru.hh.plugins.models.CheckBoxListViewItem

data class ModuleDisplayableItem(
    override val text: String,
    override val isForceEnabled: Boolean,
    override var isChecked: Boolean,
    val gradleModule: Module
) : CheckBoxListViewItem