package ru.hh.android.plugin.feature_module.core.ui

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import ru.hh.android.plugin.feature_module.extensions.replaceLineBreaks
import javax.swing.event.HyperlinkEvent

object NotificationsFactory : NotificationListener {

    private const val NOTIFICATIONS_TITLE = "HH Feature Module"
    private const val LOGGING_NOTIFICATION_GROUP_ID = "$NOTIFICATIONS_TITLE (Logging)"
    private const val ERROR_NOTIFICATION_GROUP_ID = "$NOTIFICATIONS_TITLE (Errors)"


    private val loggingNotificationGroup = NotificationGroup.logOnlyGroup(LOGGING_NOTIFICATION_GROUP_ID)
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
                .notify(null)
    }

}