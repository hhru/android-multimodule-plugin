package ru.hh.android.plugins.android_feature_module.models.converters

import com.intellij.openapi.module.Module
import ru.hh.android.plugins.android_feature_module.models.ModuleListItem

class ModuleConverter {

    fun convert(items: List<Module>): MutableList<ModuleListItem> = items.mapTo(mutableListOf()) { convert(it) }


    private fun convert(item: Module): ModuleListItem {
        return ModuleListItem(
                text = item.name,
                readmeText = "",
                isEnabled = false,
                gradleModule = item
        )
    }

}