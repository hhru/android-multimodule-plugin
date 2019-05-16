package ru.hh.android.plugin.wizard.step.choose_applications.model.converter

import com.intellij.openapi.module.Module
import ru.hh.android.plugin.wizard.step.choose_applications.model.AppModuleDisplayableItem


class AppModuleConverter {

    fun convert(modules: List<Module>): List<AppModuleDisplayableItem> {
        return modules.map { convert(it) }
    }


    private fun convert(module: Module): AppModuleDisplayableItem {
        return AppModuleDisplayableItem(
                text = module.name,
                isChecked = false,
                gradleModule = module
        )
    }

}