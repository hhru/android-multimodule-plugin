package ru.hh.plugins.garcon.services

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import ru.hh.plugins.garcon.config.editor.GarconPluginSettings
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications

class GarconStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        DumbService.getInstance(project).runWhenSmart {
            setupLogger(project)
            setupNotifications(project)
        }
    }

    private fun setupLogger(project: Project) {
        val garconConfig = GarconPluginSettings.getConfig(project)
        HHLogger.plant(project, "GarconLog", garconConfig.isDebugEnabled)
    }

    private fun setupNotifications(project: Project) {
        HHNotifications.plant(project, "Garcon")
    }

}
