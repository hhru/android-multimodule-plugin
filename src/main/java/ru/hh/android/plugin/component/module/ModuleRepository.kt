package ru.hh.android.plugin.component.module

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.extensions.getExistingModules


class ModuleRepository(
        val project: Project
) : ProjectComponent {

    fun getExistingModules(): List<Module> {
        return project.getExistingModules()
    }

}