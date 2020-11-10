package ru.hh.android.plugins.garcon.extensions

import com.intellij.openapi.project.Project
import ru.hh.android.plugins.garcon.GarconEventLogger
import ru.hh.android.plugins.garcon.notifications.NotificationsFactory
import ru.hh.android.plugins.garcon.utils.GarconBundle


fun Project.infoNotification(
    message: String,
    title: String = GarconBundle.message("garcon.notifications.title")
) {
    val notificationsFactory = this.getComponent(NotificationsFactory::class.java)
    notificationsFactory.info(message, title)
}

fun Project.errorNotification(
    message: String,
    title: String = GarconBundle.message("garcon.notifications.title")
) {
    val notificationsFactory = this.getComponent(NotificationsFactory::class.java)
    notificationsFactory.error(message, title)
}

fun Project.logDebug(message: String) {
    val eventLogger = this.getComponent(GarconEventLogger::class.java)
    eventLogger.debug(message)
}

fun Project.logInfo(message: String) {
    val eventLogger = this.getComponent(GarconEventLogger::class.java)
    eventLogger.info(message)
}

fun Project.logError(message: String) {
    val eventLogger = this.getComponent(GarconEventLogger::class.java)
    eventLogger.error(message)
}