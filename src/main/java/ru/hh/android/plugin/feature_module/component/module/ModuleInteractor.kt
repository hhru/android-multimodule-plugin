package ru.hh.android.plugin.feature_module.component.module

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.feature_module.extensions.getExistingModules

class ModuleInteractor(
        private val project: Project
) : ProjectComponent {

    fun getLibrariesModules() {
        val existingModules = project.getExistingModules()
    }

    fun getApplicationModules() {

    }


    private fun filterLibrariesModules(modules: List<Module>): List<Module> {


        return modules
    }

}