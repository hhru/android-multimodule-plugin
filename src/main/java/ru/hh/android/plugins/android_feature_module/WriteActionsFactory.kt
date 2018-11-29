package ru.hh.android.plugins.android_feature_module

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project

object WriteActionsFactory {

    fun runWriteAction(project: Project, actionDescription: String, action: Runnable) {
        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().executeCommand(project, action, actionDescription, null)
        }
    }

}