package ru.hh.android.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.hh.android.plugin.extensions.canReachKotlinDataClass


/**
 * Abstract action which is enabled only for Kotlin data classes.
 */
abstract class KotlinDataClassAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = e.canReachKotlinDataClass()
    }

}