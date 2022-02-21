package ru.hh.plugins.garcon.extensions

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

fun Project.showErrorDialog(message: String) {
    Messages.showErrorDialog(this, message, "Error!")
}
