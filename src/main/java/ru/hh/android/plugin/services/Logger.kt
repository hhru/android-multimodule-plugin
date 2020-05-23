package ru.hh.android.plugin.services

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.config.PluginConfig


@Service
class Logger(
    private val project: Project
) {

    companion object {
        private const val EVENT_TITLE = "Geminio"

        fun getInstance(project: Project): Logger = project.service()

    }


    fun debug(message: String) {
        if (PluginConfig.getInstance(project).isDebugModeEnabled) {
            Notifications.Bus.notify(AntiroutineEventNotification(message))
            println(message)
        }
    }

    fun info(message: String) {
        Notifications.Bus.notify(AntiroutineEventNotification(message))
    }

    fun error(message: String) {
        Notifications.Bus.notify(AntiroutineErrorEventNotification(message))
    }


    private class AntiroutineEventNotification(content: String) : Notification(
        Notifications.SYSTEM_MESSAGES_GROUP_ID,
        EVENT_TITLE,
        content,
        NotificationType.INFORMATION
    )

    private class AntiroutineErrorEventNotification(content: String) : Notification(
        Notifications.SYSTEM_MESSAGES_GROUP_ID,
        EVENT_TITLE,
        content,
        NotificationType.ERROR
    )

}