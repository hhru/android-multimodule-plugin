package ru.hh.plugins.utils.recents_manager

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.ui.RecentsManager


object RecentsUtils {

    fun putRecentsEntry(project: Project, key: String, value: String) {
        RecentsManager.getInstance(project).registerRecentEntry(key, value)
    }

    fun putProperty(key: String, value: String) {
        PropertiesComponent.getInstance().setValue(key, value)
    }

    fun getBooleanFromProperties(key: String, defaultValue: Boolean = true): Boolean {
        return PropertiesComponent.getInstance().getBoolean(key, defaultValue)
    }

}