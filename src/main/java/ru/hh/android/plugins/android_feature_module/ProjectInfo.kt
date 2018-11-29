package ru.hh.android.plugins.android_feature_module

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.impl.ModuleImpl
import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import java.io.File

object ProjectInfo {

    private const val BUILD_GRADLE_FILE_NAME = "build.gradle"
    private const val APPLY_KEYWORD = "apply"
    private const val PLUGIN_KEYWORD = "plugin"
    private const val ANDROID_LIBRARY_PACKAGE_NAME = "com.android.library"


    private lateinit var project: Project
    private lateinit var rootModuleDirPath: String


    fun init(project: Project) {
        this.project = project
        saveRootModuleDirPath(project)
    }


    fun getProject(): Project = project

    fun getAllExistingModules(): List<Module> {
        return getAllExistingModules(project)
    }

    fun getAndroidLibrariesModules(modules: List<Module>): List<Module> {
        return modules.filter { module ->
            val buildGradlePSI = FilenameIndex.getFilesByName(
                    project,
                    BUILD_GRADLE_FILE_NAME,
                    module.moduleContentScope
            ).first()

            buildGradlePSI.children.any { psiElement ->
                val text = psiElement.text

                text.contains(APPLY_KEYWORD)
                        && text.contains(PLUGIN_KEYWORD)
                        && text.contains(ANDROID_LIBRARY_PACKAGE_NAME)
            }
        }
    }


    fun getRootModuleDirPath(): String = rootModuleDirPath

    fun getRootModule(project: Project): Module {
        return ModuleManager.getInstance(project).modules.first { it.name == project.name }
    }


    private fun getAllExistingModules(project: Project): List<Module> {
        val modules = ModuleManager.getInstance(project).modules.toList()
        return modules.subList(1, modules.size)
    }

    private fun saveRootModuleDirPath(project: Project) {
        val rootModule = getRootModule(project)
        val rootModuleFile = File(rootModule.moduleFilePath)

        rootModuleDirPath = rootModuleFile.parent
    }

}