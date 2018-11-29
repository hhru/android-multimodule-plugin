package ru.hh.android.plugins.android_feature_module.models

import com.intellij.openapi.module.Module
import ru.hh.android.plugins.android_feature_module.models.ui.CheckBoxItem


data class ModuleListItem(
        override val text: String,
        override var isEnabled: Boolean,
        val readmeText: String,
        val gradleModule: Module
) : CheckBoxItem