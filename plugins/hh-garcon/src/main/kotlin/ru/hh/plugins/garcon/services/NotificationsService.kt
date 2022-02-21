package ru.hh.plugins.garcon.services

import com.intellij.openapi.project.Project
import ru.hh.plugins.garcon.GarconConstants
import ru.hh.plugins.utils.notifications.NotificationsFactory

private const val NOTIFICATIONS_ID = "ru.hh.plugins.garcon.notifications"

private const val INFO_NOTIFICATION_GROUP_ID = "$NOTIFICATIONS_ID.info"
private const val ERROR_NOTIFICATION_GROUP_ID = "$NOTIFICATIONS_ID.error"

fun Project.balloonInfo(title: String = GarconConstants.DEFAULT_GARCON_NOTIFICATIONS_TITLE, message: String) {
    NotificationsFactory.balloonInfo(
        project = this,
        infoNotificationGroupId = INFO_NOTIFICATION_GROUP_ID,
        title = title,
        message = message
    )
}

fun Project.balloonError(title: String = GarconConstants.DEFAULT_GARCON_NOTIFICATIONS_TITLE, message: String) {
    NotificationsFactory.balloonError(
        project = this,
        errorNotificationGroupId = ERROR_NOTIFICATION_GROUP_ID,
        title = title,
        message = message
    )
}
