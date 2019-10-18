package ru.hh.android.plugin.component.module

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.extensions.getExistingModules
import ru.hh.android.plugin.extensions.isLibraryModule


class ModuleRepository(
        private val project: Project
) : ProjectComponent {

    fun getLibrariesModules(): List<Module> {
        return getExistingModules().filter { it.isLibraryModule() }
    }

    fun getApplicationModules(): List<Module> {
        val existingModules = getExistingModules()
        val librariesModules = existingModules.filter { it.isLibraryModule() }

        return existingModules.minus(librariesModules)
    }


    private fun getExistingModules(): List<Module> {
        return project.getExistingModules()
    }

}