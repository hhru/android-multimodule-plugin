package ru.hh.plugins.geminio.services

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.WindowManager
import ru.hh.plugins.geminio.ActionsHelper
import ru.hh.plugins.geminio.config.editor.GeminioPluginSettings
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JFrame

/**
 * This code will be executed on project's startup.
 */
class GeminioStartupActivity : StartupActivity {

    private var windowListener: WindowListener? = null
    private val lastViewedProject = AtomicReference<Project>(null)

    override fun runActivity(project: Project) {
        DumbService.getInstance(project).runWhenSmart {
            setupLogger(project)
            setupNotifications(project)

            HHLogger.d("Begin startup activity")

            rescanTemplateActions(project)

            windowListener = createWindowListener(project)
            getCurrentFrame(project)?.addWindowListener(windowListener)

            HHLogger.d("End startup activity")
        }
    }

    private fun getCurrentFrame(project: Project): JFrame? = WindowManager.getInstance().getFrame(project)

    private fun rescanTemplateActions(project: Project) {
        lastViewedProject.set(project)

        val actionsHelper = ActionsHelper()
        actionsHelper.resetGeminioActions(project)
        actionsHelper.createGeminioActions(project)
    }

    private fun createWindowListener(project: Project): WindowListener {
        return object : WindowListener {
            override fun windowClosed(e: WindowEvent?) {
                windowListener?.also { listener ->
                    getCurrentFrame(project)?.removeWindowListener(listener)
                }
            }

            override fun windowActivated(e: WindowEvent?) {
                val lastProject = lastViewedProject.get()
                if (lastProject != project) {
                    HHLogger.d(
                        "Project changed -> rescan Geminio's actions " +
                            "[old project name: `${lastProject.name}`, new project.name: `${project.name}`]"
                    )
                    rescanTemplateActions(project)
                } else {
                    HHLogger.d(
                        "Activated project is the same as previous -> " +
                            "no need to rescan Geminio's actions [project.name: `${project.name}`]"
                    )
                }
            }

            override fun windowDeactivated(e: WindowEvent?) = Unit
            override fun windowIconified(e: WindowEvent?) = Unit
            override fun windowDeiconified(e: WindowEvent?) = Unit
            override fun windowOpened(e: WindowEvent?) = Unit
            override fun windowClosing(e: WindowEvent?) = Unit
        }
    }

    private fun setupLogger(project: Project) {
        val garconConfig = GeminioPluginSettings.getConfig(project)
        HHLogger.plant(project, "GeminioLog", garconConfig.isDebugEnabled)
    }

    private fun setupNotifications(project: Project) {
        HHNotifications.plant(project, "Geminio")
    }
}
