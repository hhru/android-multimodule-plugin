package ru.hh.android.plugin.component

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.extensions.replaceLineBreaks
import javax.swing.event.HyperlinkEvent


class NotificationsFactory(
        private val project: Project
) : ProjectComponent, NotificationListener {

    companion object {
        private const val NOTIFICATIONS_TITLE = "HH Feature Module"
        private const val LOGGING_NOTIFICATION_GROUP_ID = "$NOTIFICATIONS_TITLE (Logging)"
        private const val ERROR_NOTIFICATION_GROUP_ID = "$NOTIFICATIONS_TITLE (Errors)"
    }

    private val loggingNotificationGroup = NotificationGroup.balloonGroup(LOGGING_NOTIFICATION_GROUP_ID)
    private val errorsNotificationGroup = NotificationGroup.balloonGroup(ERROR_NOTIFICATION_GROUP_ID)


    override fun hyperlinkUpdate(notification: Notification, event: HyperlinkEvent) {
        // do nothing by default.
    }


    fun info(message: String) {
        showNotification(loggingNotificationGroup, NotificationType.INFORMATION, message)
    }

    fun error(message: String) {
        showNotification(errorsNotificationGroup, NotificationType.ERROR, message)
    }


    private fun showNotification(
            notificationGroup: NotificationGroup,
            notificationType: NotificationType,
            message: String
    ) {
        notificationGroup.createNotification(
                NOTIFICATIONS_TITLE,
                message.replaceLineBreaks(),
                notificationType,
                this
        )
                .notify(project)
    }

}