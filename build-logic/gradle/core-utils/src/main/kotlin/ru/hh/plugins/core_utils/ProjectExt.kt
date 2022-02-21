package ru.hh.plugins.core_utils

import org.gradle.api.Project

fun Project.isRoot() = project == project.rootProject

fun Project.getMandatoryStringProperty(name: String): String {
    return if (hasProperty(name)) {
        val string = property(name)?.toString()
        if (string.isNullOrBlank()) {
            throw RuntimeException("Parameter: $name is blank but required")
        } else {
            string
        }
    } else {
        throw RuntimeException("Parameter: $name is missing but required")
    }
}
