package ru.hh.android.plugin.utils

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.services.NotificationsFactory


fun Project.notifyInfo(message: String) {
    logInfo(message)
    NotificationsFactory.newInstance(this).info(message)
}

fun Project.notifyError(message: String) {
    logError(message)
    NotificationsFactory.newInstance(this).error(message)
}