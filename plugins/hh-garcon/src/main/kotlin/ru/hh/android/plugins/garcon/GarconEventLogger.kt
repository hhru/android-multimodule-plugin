package ru.hh.android.plugins.garcon

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import ru.hh.android.plugins.garcon.config.PluginConfig


class GarconEventLogger(
    private val project: Project
) : ProjectComponent {

    companion object {
        private const val EVENT_TITLE = "Garcon"
    }


    fun debug(message: String) {
        if (PluginConfig.getInstance(project).enableDebugMode) {
            Notifications.Bus.notify(GarconEventNotification(message))
        }
    }

    fun info(message: String) {
        Notifications.Bus.notify(GarconEventNotification(message))
    }

    fun error(message: String) {
        Notifications.Bus.notify(GarconErrorEventNotification(message))
    }


    private class GarconEventNotification(content: String) : Notification(
        Notifications.SYSTEM_MESSAGES_GROUP_ID,
        EVENT_TITLE,
        content,
        NotificationType.INFORMATION
    )

    private class GarconErrorEventNotification(content: String) : Notification(
        Notifications.SYSTEM_MESSAGES_GROUP_ID,
        EVENT_TITLE,
        content,
        NotificationType.ERROR
    )

}