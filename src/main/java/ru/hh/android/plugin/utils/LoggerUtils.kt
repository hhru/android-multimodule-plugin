package ru.hh.android.plugin.utils

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.services.Logger


fun Project.logDebug(message: String) {
    service<Logger>().debug(message)
}

fun Project.logInfo(message: String) {
    service<Logger>().info(message)
}

fun Project.logError(message: String) {
    service<Logger>().error(message)
}