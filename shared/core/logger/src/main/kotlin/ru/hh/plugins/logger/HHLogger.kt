package ru.hh.plugins.logger

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
 *                             displayType="NONE" />
 *      <extensions/>
 * </code>
 *
 * 2) Init `HHLogger` with `Project` somewhere, e.g. in `postStartupActivity`.
 *
 * 3) Use `HHLogger` every time you need to log something.
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

        @Volatile
        private var prefix: String = ""

        fun plant(project: Project, prefix: String, isDebugEnabled: Boolean) {
            this.project = project
            this.prefix = prefix
            this.isDebugEnabled.set(isDebugEnabled)
        }

        fun enableDebug(enable: Boolean) {
            this.isDebugEnabled.set(enable)
        }

        fun d(message: String) {
            println("$prefix [DEBUG] $message")
            ideaLogger.debug(message)
            if (isDebugEnabled.get()) {
                sendToEventLog(message, NotificationType.INFORMATION)
            }
        }

        fun i(message: String) {
            println("$prefix [INFO] $message")
            ideaLogger.info(message)
            if (isDebugEnabled.get()) {
                sendToEventLog(message, NotificationType.INFORMATION)
            }
        }

        fun e(message: String) {
            println("$prefix [ERROR] $message")
            ideaLogger.error(message)
            if (isDebugEnabled.get()) {
                sendToEventLog(message, NotificationType.ERROR)
            }
        }

        private fun sendToEventLog(message: String, type: NotificationType) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup(GROUP_ID)
                .createNotification(message, type)
                .notify(project)
        }
    }
}
