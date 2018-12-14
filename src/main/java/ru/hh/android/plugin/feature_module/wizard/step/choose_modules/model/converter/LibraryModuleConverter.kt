package ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model.converter

import com.intellij.openapi.module.Module
import ru.hh.android.plugin.feature_module.wizard.step.choose_modules.model.LibraryModuleDisplayableItem

class LibraryModuleConverter {

    fun convert(libraries: List<Module>, forceEnabledModulesNames: Set<String>): List<LibraryModuleDisplayableItem> {
        return libraries.map { convert(it, forceEnabledModulesNames) }
    }


    private fun convert(library: Module, forceEnabledModulesNames: Set<String>): LibraryModuleDisplayableItem {
        val isForceEnabled = forceEnabledModulesNames.contains(library.name)

        return LibraryModuleDisplayableItem(
                text = library.name,
                isForceEnabled = isForceEnabled,
                isChecked = isForceEnabled
        )
    }

}