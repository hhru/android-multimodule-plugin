package ru.hh.plugins.extensions.openapi

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.util.projectStructure.allModules
import ru.hh.plugins.extensions.SPACE
import ru.hh.plugins.extensions.UNDERSCORE


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
 * Fetch all gradle modules in project.
 * You need additional filtration if you want to get only libraries, or only apps.
 */
fun Project.getExistingModules(): List<Module> {
    return ModuleManager.getInstance(this).modules.toList().filter { it.name != this.name }
}

/**
 * Fetch all android applications modules in project.
 *
 * Application module - module with applied `com.android.application` gradle plugin.
 */
fun Project.getAndroidApplicationsModules(): List<Module> {
    return allModules().filter { it.isAndroidAppModule() }
}

/**
 * Fetch all libraries modules in project.
 *
 * Library module - module with applied `com.android.library` or `java-library` gradle plugins.
 */
fun Project.getLibrariesModules(): List<Module> {
    return getExistingModules().filter { it.isAndroidLibraryModule() }
}

fun Project.getRootModule(): Module {
    return ModuleManager.getInstance(this).modules.toList()
        .first { it.name == this.name || it.name == this.name.replace(Char.SPACE, Char.UNDERSCORE) }
}