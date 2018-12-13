package ru.hh.android.plugin.feature_module.extensions

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project


fun Project.getExistingModules(): List<Module> {
    return ModuleManager.getInstance(this).modules.toList().filter { it.name != this.name }
}