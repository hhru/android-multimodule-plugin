package ru.hh.android.plugin.extensions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import java.io.File


fun Project.getExistingModules(): List<Module> {
    return ModuleManager.getInstance(this).modules.toList().filter { it.name != this.name }
}

fun Project.getRootModulePath(): String {
    val file = File(getRootModule().moduleFilePath)
    return file.parent
}

fun Project.getRootModule(): Module {
    return ModuleManager.getInstance(this).modules.toList().first { it.name == this.name }
}

inline fun Project.runWriteAction(
        description: String = String.EMPTY,
        crossinline action: () -> Unit
) {
    ApplicationManager.getApplication().runWriteAction {
        CommandProcessor.getInstance().executeCommand(this, { action.invoke() }, description, null)
    }
}