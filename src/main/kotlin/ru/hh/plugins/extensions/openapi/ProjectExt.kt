package ru.hh.plugins.extensions.openapi

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import ru.hh.plugins.extensions.SPACE
import ru.hh.plugins.extensions.UNDERSCORE
import ru.hh.plugins.logger.HHLogger

fun Project.getRootModule(): Module {
    val moduleManager = ModuleManager.getInstance(this)
    val modules = moduleManager.modules.toList()
    HHLogger.d(
        """
        Project.getRootModule
        
        this: $this
        this.name: ${this.name}
        this.projectFilePath: ${this.projectFilePath}
        this.basePath: ${this.basePath}
        this.isInitialized: ${this.isInitialized}
        this.isOpen: ${this.isOpen}
        """.trimIndent()
    )

    val modulesNames = modules.joinToString(separator = "\n") { it.name }

    HHLogger.d("===== Modules names ===== ")
    HHLogger.d(modulesNames)

    val rootModule = modules.firstOrNull {
        it.name == this.name || it.name == this.name.replace(Char.SPACE, Char.UNDERSCORE)
    }

    HHLogger.d("rootModule == $rootModule")

    return rootModule!!
}
