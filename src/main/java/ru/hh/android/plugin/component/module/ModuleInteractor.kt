package ru.hh.android.plugin.component.module

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.Module
import ru.hh.android.plugin.extensions.isLibraryModule


class ModuleInteractor(
        private val moduleRepository: ModuleRepository
) : ProjectComponent {

    fun getLibrariesModules(): List<Module> {
        return moduleRepository.getExistingModules().filter { it.isLibraryModule() }
    }

    fun getApplicationModules(): List<Module> {
        val existingModules = moduleRepository.getExistingModules()
        val librariesModules = existingModules.filter { it.isLibraryModule() }

        return existingModules.minus(librariesModules)
    }

}