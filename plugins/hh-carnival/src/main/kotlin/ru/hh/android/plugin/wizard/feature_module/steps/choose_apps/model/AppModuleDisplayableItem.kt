package ru.hh.android.plugin.wizard.feature_module.steps.choose_apps.model

import com.intellij.openapi.module.Module
import ru.hh.plugins.models.CheckBoxListViewItem


data class AppModuleDisplayableItem(
    override val text: String,
    override val isForceEnabled: Boolean = false,
    override var isChecked: Boolean,
    val gradleModule: Module
) : CheckBoxListViewItem