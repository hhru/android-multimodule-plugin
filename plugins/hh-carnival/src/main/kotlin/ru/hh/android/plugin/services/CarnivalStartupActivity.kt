package ru.hh.android.plugin.services

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import ru.hh.android.plugin.config.CarnivalPluginConfig
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications

class CarnivalStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        DumbService.getInstance(project).runWhenSmart {
            setupLogger(project)
            setupNotifications(project)
        }
    }

    private fun setupLogger(project: Project) {
        val config = CarnivalPluginConfig.getInstance(project)
        HHLogger.plant(project, "CarnivalLog", config.isDebugEnabled)
    }

    private fun setupNotifications(project: Project) {
        HHNotifications.plant(project, "Carnival")
    }

}
