package ru.hh.android.plugin.actions.modules.copy_module.model

import com.intellij.openapi.module.Module
import org.jetbrains.android.facet.AndroidFacet


data class NewModuleParams(
    val newModuleName: String,
    val newPackageName: String,
    val appModule: Module,
    val moduleToCopyFacet: AndroidFacet
)