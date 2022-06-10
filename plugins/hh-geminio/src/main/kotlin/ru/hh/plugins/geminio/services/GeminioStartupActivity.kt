package ru.hh.plugins.geminio.services

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import ru.hh.plugins.geminio.ActionsCreator

/**
 * This code will be executed on project's startup.
 */
class GeminioStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        DumbService.getInstance(project).runWhenSmart {
            println("GeminioStartupActivity::")

            ActionsCreator().create(project)

            println("GeminioStartupActivity::END")
            println("==============================================")
        }
    }

}
