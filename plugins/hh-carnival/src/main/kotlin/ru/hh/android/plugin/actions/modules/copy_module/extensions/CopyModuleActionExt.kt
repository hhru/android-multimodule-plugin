package ru.hh.android.plugin.actions.modules.copy_module.extensions

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import ru.hh.android.plugin.actions.modules.copy_module.model.CopyModuleActionData


val CopyModuleActionData.moduleToCopy: Module get() = androidFacet.module

val CopyModuleActionData.project: Project get() = moduleToCopy.project