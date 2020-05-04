package ru.hh.android.plugin.utils

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.services.Logger


fun Project.logDebug(message: String) {
    Logger.newInstance(this).debug(message)
}

fun Project.logInfo(message: String) {
    Logger.newInstance(this).info(message)
}

fun Project.logError(message: String) {
    Logger.newInstance(this).error(message)
}