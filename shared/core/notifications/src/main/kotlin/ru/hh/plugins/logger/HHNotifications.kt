package ru.hh.plugins.logger

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project

/**
 * Common notifications interface for every plugin.
 *
 * To use in plugins you need:
 *
 * 1) Declare notification group as extension in `plugin.xml`:
 *
 * <code>
 *      <extensions defaultExtensionNs="com.intellij">
 *          <notificationGroup id="ru.hh.plugins.notifications"
 *                             displayType="BALLOON" />
 *      <extensions/>
 * </code>
 *
 * 2) Init `HHNotifications` with `Project` somewhere, e.g. in `postStartupActivity`.
 *
 * 3) Use `HHNotifications` every time you need to show balloon notifications:
 *
 * <code>
 *     HHNotifications.info("message info")
 *     HHNotifications.warning("message warning")
 *     HHLogger.error("message error")
 * </code>
 */
class HHNotifications private constructor() {

    companion object {

        private const val GROUP_ID = "ru.hh.plugins.notifications"

        @Volatile
        private var project: Project? = null

        @Volatile
        private var title: String = ""

        fun plant(project: Project, notificationsTitle: String) {
            this.project = project
            this.title = notificationsTitle
        }

        fun info(message: String) {
            println("[$title] [INFO] $message")
            sendNotification(message, NotificationType.INFORMATION)
        }

        fun warning(message: String) {
            println("[$title] [WARNING] $message")
            sendNotification(message, NotificationType.WARNING)
        }

        fun error(message: String) {
            println("[$title] [ERROR] $message")
            sendNotification(message, NotificationType.ERROR)
        }

        fun error(message: String, action: AnAction) {
            println("[$title] [ERROR (with action)] $message")
            sendNotification(message, NotificationType.ERROR, action)
        }

        private fun sendNotification(message: String, type: NotificationType, action: AnAction? = null) {
            val notification = NotificationGroupManager.getInstance()
                .getNotificationGroup(GROUP_ID)
                .createNotification(title, message, type)
            action?.let { notification.addAction(action) }

            notification.notify(project)
        }
    }
}
