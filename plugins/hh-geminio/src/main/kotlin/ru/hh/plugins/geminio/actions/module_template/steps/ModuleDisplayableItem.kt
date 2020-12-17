package ru.hh.plugins.geminio.actions.module_template.steps

import com.intellij.openapi.module.Module
import ru.hh.plugins.models.CheckBoxListViewItem


data class ModuleDisplayableItem(
    override val text: String,
    override var isChecked: Boolean,
    val gradleModule: Module
) : CheckBoxListViewItem {

    override val isForceEnabled: Boolean = false

}