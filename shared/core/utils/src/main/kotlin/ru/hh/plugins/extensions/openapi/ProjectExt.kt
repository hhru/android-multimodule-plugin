package ru.hh.plugins.extensions.openapi

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.psi.codeStyle.CodeStyleManager
import ru.hh.plugins.extensions.SPACE
import ru.hh.plugins.extensions.UNDERSCORE
import ru.hh.plugins.logger.HHLogger

/**
 * You can use this method for adding code without applying code style.
 * When you generate code with fully qualified class names your lines can be much more longer than max line length
 * defined in your code style.
 *
 * [com.intellij.psi.PsiElement.addAfter] or [com.intellij.psi.PsiElement.addBefore] by default adds
 * new element with code style applying -> some lines can be added with wrong indents and line breaks.
 *
 * @param action - action for execution. Cannot be inlined.
 */
fun Project.executeWithoutCodeStyle(action: () -> Unit) {
    CodeStyleManager.getInstance(this).performActionWithFormatterDisabled(action)
}

/**
 * Fetch all android applications modules in project.
 *
 * Application module - module with applied `com.android.application` gradle plugin.
 */
fun Project.getAndroidApplicationsModules(): List<Module> {
    return modules.asList().filter { it.isAndroidAppModule() }
}

/**
 * Fetch all libraries modules in project.
 *
 * Library module - module with applied `com.android.library` or `java-library` gradle plugins.
 */
fun Project.getLibrariesModules(): List<Module> {
    return modules.asList().filter { it.isAndroidLibraryModule() }
}

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
