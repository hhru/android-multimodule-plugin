package ru.hh.android.plugin.services.modules

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.extensions.getExistingModules
import ru.hh.android.plugin.extensions.isAppModule
import ru.hh.android.plugin.extensions.isLibraryModule


@Service
class ModuleRepository(
    private val project: Project
) {

    companion object {
        fun getInstance(project: Project): ModuleRepository = project.service()
    }

    fun fetchAppModules(): List<Module> {
        return project.getExistingModules().filter { it.isAppModule() }
    }

    fun fetchLibrariesModules(): List<Module> {
        return project.getExistingModules().filter { it.isLibraryModule() }
    }

}