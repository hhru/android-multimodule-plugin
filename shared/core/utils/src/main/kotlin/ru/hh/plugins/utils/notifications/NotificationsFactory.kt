package ru.hh.plugins.utils.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
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
        message: String,
        action: AnAction? = null,
    ) {
        showNotification(project, infoNotificationGroupId, NotificationType.INFORMATION, title, message, action)
    }

    fun balloonError(
        project: Project,
        errorNotificationGroupId: String,
        title: String,
        message: String,
        action: AnAction? = null,
    ) {
        showNotification(project, errorNotificationGroupId, NotificationType.ERROR, title, message, action)
    }

    private fun showNotification(
        project: Project,
        notificationGroupId: String,
        notificationType: NotificationType,
        title: String,
        message: String,
        action: AnAction?,
    ) {
        val notificationGroup = NotificationGroup.balloonGroup(notificationGroupId)
        notificationGroup.createNotification(
            title,
            message.replaceLineBreaks(),
            notificationType,
            this
        ).apply {
            action?.also(::addAction)
            notify(project)
        }
    }
}
