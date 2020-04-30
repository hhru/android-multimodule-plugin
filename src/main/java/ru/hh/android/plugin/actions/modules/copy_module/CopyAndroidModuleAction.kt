package ru.hh.android.plugin.actions.modules.copy_module

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.actions.modules.copy_module.view.CopyAndroidModuleActionDialog
import ru.hh.android.plugin.extensions.androidFacet
import ru.hh.android.plugin.extensions.isLibraryModule
import ru.hh.android.plugin.services.NotificationsFactory
import ru.hh.android.plugin.utils.PluginBundle


/**
 * Action for copy module.
 */
class CopyAndroidModuleAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = when {
            e.androidFacet?.module?.isLibraryModule() == false -> false
            else -> true
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { project ->
            e.androidFacet?.module?.name?.let { moduleName ->
                handleAction(project, moduleName)
            }
        }
    }


    private fun handleAction(project: Project, moduleName: String) {
        val dialog = CopyAndroidModuleActionDialog(project, moduleName)
        dialog.show()

        if (dialog.isOK.not()) {
            project.service<NotificationsFactory>().error(
                    PluginBundle.message("geminio.notifications.copy_module.cancel")
            )
            return
        }

        println("New module name: ${dialog.getModuleName()}, package name: ${dialog.getPackageName()}")
        // TODO обработка копирования
    }

}