package ru.hh.plugins.geminio.services.android

import com.android.tools.idea.gradle.actions.SyncProjectAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.UIUtil

internal fun Project.showAndroidSyncQuestionDialog(syncPerformedActionEvent: AnActionEvent) {
    Messages.showOkCancelDialog(
        this,
        "Sync Project with Gradle files?",
        "Sync Project",
        "Sync",
        "Cancel Without Sync",
        UIUtil.getQuestionIcon(),
    ).also { answer ->
        if (answer == Messages.OK) {
            SyncProjectAction().actionPerformed(syncPerformedActionEvent)
        }
    }
}

