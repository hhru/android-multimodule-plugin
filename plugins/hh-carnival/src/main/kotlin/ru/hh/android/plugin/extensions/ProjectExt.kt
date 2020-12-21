package ru.hh.android.plugin.extensions

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.extensions.openapi.getRootModule
import java.io.File


fun Project.getRootModulePath(): String {
    val file = File(getRootModule().moduleFilePath)
    return file.parent
}

inline fun Project.runWriteAction(
    description: String = String.EMPTY,
    crossinline action: () -> Unit
) {
    ApplicationManager.getApplication().runWriteAction {
        CommandProcessor.getInstance().executeCommand(this, { action.invoke() }, description, null)
    }
}