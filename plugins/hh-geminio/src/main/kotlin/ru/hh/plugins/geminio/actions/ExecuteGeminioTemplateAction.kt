package ru.hh.plugins.geminio.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


/**
 * Base action for executing templates from YAML config.
 *
 * This action not registered in plugin.xml, because we create it in runtime.
 */
@Suppress("ComponentNotRegistered")
class ExecuteGeminioTemplateAction(
    private val actionText: String,
    private val actionDescription: String,
    private val actionHandler: (event: AnActionEvent) -> Unit
) : AnAction() {

    companion object {
        const val BASE_ID = "ru.hh.plugins.geminio.actions."
    }

    init {
        with(templatePresentation) {
            text = actionText
            description = actionDescription
            isEnabledAndVisible = true
        }
    }


    override fun actionPerformed(e: AnActionEvent) {
        actionHandler.invoke(e)
    }

}