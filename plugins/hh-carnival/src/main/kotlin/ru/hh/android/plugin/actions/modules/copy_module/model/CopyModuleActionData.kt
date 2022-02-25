package ru.hh.android.plugin.actions.modules.copy_module.model

import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.android.facet.AndroidFacet

data class CopyModuleActionData(
    val actionEvent: AnActionEvent,
    val androidFacet: AndroidFacet
)
