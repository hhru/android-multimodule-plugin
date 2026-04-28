package ru.hh.plugins.geminio.services.android

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules

/**
 * Returns Android application modules available in the current project.
 */
fun Project.getAndroidApplicationsModules(): List<Module> {
    return modules.asList().filter { it.isAndroidAppModule() }
}
