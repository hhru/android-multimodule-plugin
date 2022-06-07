package ru.hh.plugins.dialog.sync

import com.android.tools.idea.gradle.actions.SyncProjectAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.UIUtil
import org.jetbrains.kotlin.lombok.utils.capitalize

fun Project.showSyncQuestionDialog(syncPerformedActionEvent: AnActionEvent) {
    Messages.showOkCancelDialog(
        this,
        "Sync Project with Gradle files?",
        "Sync Project",
        "Sync",
        "Cancel",
        UIUtil.getQuestionIcon(),
    ).also { answer ->
        if (answer == Messages.OK) SyncProjectAction().actionPerformed(syncPerformedActionEvent)
    }
}
