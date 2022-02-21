package ru.hh.android.plugin.utils

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.services.NotificationsFactory

fun Project.notifyInfo(message: String) {
    NotificationsFactory.getInstance(this).info(message)
}

fun Project.notifyError(message: String) {
    NotificationsFactory.getInstance(this).error(message)
}
