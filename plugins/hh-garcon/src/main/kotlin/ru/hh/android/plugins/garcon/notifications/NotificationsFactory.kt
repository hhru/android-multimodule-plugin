package ru.hh.android.plugins.garcon.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugins.garcon.extensions.base_types.replaceLineBreaks
import javax.swing.event.HyperlinkEvent


class NotificationsFactory(
    private val project: Project
) : ProjectComponent, NotificationListener {

    companion object {
        private const val NOTIFICATIONS_ID = "ru.hh.android.plugins.garcon.notifications"

        private const val LOGGING_NOTIFICATION_GROUP_ID = "${NOTIFICATIONS_ID}.logging"
        private const val ERROR_NOTIFICATION_GROUP_ID = "${NOTIFICATIONS_ID}.errors"
    }


    private val loggingNotificationGroup = NotificationGroup.balloonGroup(LOGGING_NOTIFICATION_GROUP_ID)
    private val errorsNotificationGroup = NotificationGroup.balloonGroup(ERROR_NOTIFICATION_GROUP_ID)


    override fun hyperlinkUpdate(notification: Notification, event: HyperlinkEvent) {
        // do nothing by default.
    }




    fun info(message: String, title: String) {
        showNotification(loggingNotificationGroup, NotificationType.INFORMATION, message, title)
    }

    fun error(message: String, title: String) {
        showNotification(errorsNotificationGroup, NotificationType.ERROR, message, title)
    }


    private fun showNotification(
        notificationGroup: NotificationGroup,
        notificationType: NotificationType,
        message: String,
        title: String
    ) {
        notificationGroup.createNotification(
                title,
                message.replaceLineBreaks(),
                notificationType,
                this
            )
            .notify(project)
    }

}