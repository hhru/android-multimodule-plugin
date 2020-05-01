package ru.hh.android.plugin.actions.modules.copy_module.extensions

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import ru.hh.android.plugin.CodeGeneratorConstants
import ru.hh.android.plugin.actions.modules.copy_module.model.NewModuleParams
import ru.hh.android.plugin.extensions.rootPsiDirectory


val NewModuleParams.moduleToCopy: Module get() = moduleToCopyFacet.module

val NewModuleParams.project: Project get() = moduleToCopy.project

val NewModuleParams.moduleMainSourceSetPsiDirectory: PsiDirectory?
    get() = moduleToCopy.rootPsiDirectory
        ?.findSubdirectory(CodeGeneratorConstants.SRC_FOLDER_NAME)
        ?.findSubdirectory(CodeGeneratorConstants.MAIN_SOURCE_SET_FOLDER_NAME)
