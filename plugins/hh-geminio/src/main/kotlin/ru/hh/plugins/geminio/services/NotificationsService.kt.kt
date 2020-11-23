package ru.hh.plugins.geminio.services


import com.intellij.openapi.project.Project
import ru.hh.plugins.geminio.GeminioConstants
import ru.hh.plugins.utils.notifications.NotificationsFactory


private const val NOTIFICATIONS_ID = "ru.hh.plugins.geminio.notifications"

private const val INFO_NOTIFICATION_GROUP_ID = "$NOTIFICATIONS_ID.info"
private const val ERROR_NOTIFICATION_GROUP_ID = "$NOTIFICATIONS_ID.error"


fun Project.balloonInfo(title: String = GeminioConstants.DEFAULT_NOTIFICATIONS_TITLE, message: String) {
    NotificationsFactory.balloonInfo(
        project = this,
        infoNotificationGroupId = INFO_NOTIFICATION_GROUP_ID,
        title = title,
        message = message
    )
}

fun Project.balloonError(title: String = GeminioConstants.DEFAULT_NOTIFICATIONS_TITLE, message: String) {
    NotificationsFactory.balloonError(
        project = this,
        errorNotificationGroupId = ERROR_NOTIFICATION_GROUP_ID,
        title = title,
        message = message
    )
}