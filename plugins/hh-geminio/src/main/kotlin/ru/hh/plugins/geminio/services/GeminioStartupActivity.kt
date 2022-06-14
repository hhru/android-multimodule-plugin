package ru.hh.plugins.geminio.services

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.WindowManager
import ru.hh.plugins.geminio.ActionsHelper
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import javax.swing.JFrame

/**
 * This code will be executed on project's startup.
 */
class GeminioStartupActivity : StartupActivity {

    private var windowListener: WindowListener? = null

    override fun runActivity(project: Project) {
        DumbService.getInstance(project).runWhenSmart {
            println("GeminioStartupActivity::")

            ActionsHelper().createGeminioActions(project)

            println("GeminioStartupActivity::END")
            println("==============================================")

            windowListener = object : WindowListener {
                override fun windowClosed(e: WindowEvent?) {
                    windowListener?.also { listener ->
                        getCurrentFrame(project)?.removeWindowListener(listener)
                    }
                }

                override fun windowDeactivated(e: WindowEvent?) {
                    if (getCurrentFrame(project) != null) {
                        ActionsHelper().resetGeminioActions(project)
                    }
                }

                override fun windowActivated(e: WindowEvent?) {
                    ActionsHelper().createGeminioActions(project)
                }

                override fun windowIconified(e: WindowEvent?) = Unit
                override fun windowDeiconified(e: WindowEvent?) = Unit
                override fun windowOpened(e: WindowEvent?) = Unit
                override fun windowClosing(e: WindowEvent?) = Unit
            }

            getCurrentFrame(project)?.addWindowListener(windowListener)
        }
    }

    private fun getCurrentFrame(project: Project): JFrame? = WindowManager.getInstance().getFrame(project)

}
