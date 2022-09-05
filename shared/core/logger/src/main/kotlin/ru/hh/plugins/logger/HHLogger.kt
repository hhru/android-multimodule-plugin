package ru.hh.plugins.logger

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Common logger interface for every plugin.
 * Write messages into:
 *
 * - runtime log of plugin
 * - idea.log file
 * - Event log of current project
 *
 * To use in plugins you need:
 *
 * 1) Declare notification group as extension in `plugin.xml`:
 *
 * <code>
 *      <extensions defaultExtensionNs="com.intellij">
 *          <notificationGroup id="ru.hh.plugins.logger"
 *              displayType="BALLOON"
 *              key="logger.group.name"/>
 *      <extensions/>
 * </code>
 *
 * 2) Declare messages bundle to use `key` in `notificationGroup` in `plugin.xml`:
 *
 * <code>
 *     <resource-bundle>messages.PluginBundle</resource-bundle>
 * </code>
 *
 * There you should declare `logger.group.name` key to set up GroupId for event log's notifications.
 *
 * 3) Init `HHLogger` with `Project` somewhere, e.g. in `postStartupActivity`.
 *
 * 4) Use `HHLogger` every time you need to log something.
 *
 * <code>
 *     HHLogger.d("message debug")
 *     HHLogger.i("message info")
 *     HHLogger.e("message error")
 * </code>
 */
class HHLogger private constructor() {

    companion object {

        private const val GROUP_ID = "ru.hh.plugins.logger"

        private val isDebugEnabled = AtomicBoolean(false)
        private val ideaLogger = Logger.getInstance(HHLogger::class.java)

        @Volatile
        private var project: Project? = null


        fun plant(project: Project, isDebugEnabled: Boolean) {
            this.project = project
            this.isDebugEnabled.set(isDebugEnabled)
        }

        fun enableDebug(enable: Boolean) {
            this.isDebugEnabled.set(enable)
        }

        fun d(message: String) {
            println(message)
            ideaLogger.debug(message)
            if (isDebugEnabled.get()) {
                val notification = getLoggerNotification(message, NotificationType.INFORMATION)
                sendToEventLog(notification)
            }
        }

        fun i(message: String) {
            println("[INFO] $message")
            ideaLogger.info(message)
            if (isDebugEnabled.get()) {
                val notification = getLoggerNotification(message, NotificationType.INFORMATION)
                sendToEventLog(notification)
            }
        }

        fun e(message: String) {
            println("[ERROR] $message")
            ideaLogger.error(message)
            if (isDebugEnabled.get()) {
                val notification = getLoggerNotification(message, NotificationType.ERROR)
                sendToEventLog(notification)
            }
        }


        private fun sendToEventLog(notification: Notification) {
            notification.notify(project)
            notification.balloon?.hide()
        }

        private fun getLoggerNotification(message: String, type: NotificationType): Notification {
            return NotificationGroupManager.getInstance()
                .getNotificationGroup(GROUP_ID)
                .createNotification(message, type)
        }

    }


}
