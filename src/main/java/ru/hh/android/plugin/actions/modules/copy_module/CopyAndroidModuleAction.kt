package ru.hh.android.plugin.actions.modules.copy_module

import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.actions.AndroidModuleAction
import ru.hh.android.plugin.actions.modules.copy_module.view.CopyAndroidModuleActionDialog


/**
 * Action for copy module.
 */
class CopyAndroidModuleAction : AndroidModuleAction() {

    override fun actionPerformed(e: AnActionEvent) {

        CopyAndroidModuleActionDialog(e.project!!, "ololo").show()
    }

}