package ru.hh.plugins.utils.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import ru.hh.plugins.extensions.replaceLineBreaks
import javax.swing.event.HyperlinkEvent

object NotificationsFactory : NotificationListener {

    override fun hyperlinkUpdate(p0: Notification, p1: HyperlinkEvent) {
        // do nothing
    }

    fun balloonInfo(
        project: Project,
        infoNotificationGroupId: String,
        title: String,
        message: String
    ) {
        showNotification(project, infoNotificationGroupId, NotificationType.INFORMATION, title, message)
    }

    fun balloonError(
        project: Project,
        errorNotificationGroupId: String,
        title: String,
        message: String
    ) {
        showNotification(project, errorNotificationGroupId, NotificationType.ERROR, title, message)
    }

    private fun showNotification(
        project: Project,
        notificationGroupId: String,
        notificationType: NotificationType,
        title: String,
        message: String
    ) {
        val notificationGroup = NotificationGroup.balloonGroup(notificationGroupId)
        notificationGroup.createNotification(
            title,
            message.replaceLineBreaks(),
            notificationType,
            this
        ).notify(project)
    }
}
