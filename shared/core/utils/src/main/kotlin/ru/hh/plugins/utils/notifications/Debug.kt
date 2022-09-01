package ru.hh.plugins.utils.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications

object Debug {

    private const val GROUP_ID = "Geminio"
    private const val TITLE = "Debug"

    fun info(message: String) {
        Debug.info(message)
        Notifications.Bus.notify(
            Notification(
                GROUP_ID,
                TITLE,
                message,
                NotificationType.INFORMATION
            )
        )
    }

    fun error(message: String) {
        Debug.info("ERROR: $message")
        Notifications.Bus.notify(
            Notification(
                GROUP_ID,
                TITLE,
                message,
                NotificationType.ERROR
            )
        )
    }

}