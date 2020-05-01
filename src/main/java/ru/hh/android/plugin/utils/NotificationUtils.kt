package ru.hh.android.plugin.utils

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.services.Logger
import ru.hh.android.plugin.services.NotificationsFactory


fun Project.notifyError(message: String) {
    service<NotificationsFactory>().error(message)
    service<Logger>().error(message)
}

fun Project.notifyInfo(message: String) {
    service<NotificationsFactory>().info(message)
    service<Logger>().info(message)
}