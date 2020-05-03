package ru.hh.android.plugin.actions.modules.copy_module

import com.android.tools.idea.gradle.actions.SyncProjectAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.executeCommand
import com.intellij.openapi.components.service
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiPlainTextFile
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import ru.hh.android.plugin.CodeGeneratorConstants.ANDROID_MANIFEST_XML_FILE_NAME
import ru.hh.android.plugin.CodeGeneratorConstants.JAVA_SOURCE_FOLDER_NAME
import ru.hh.android.plugin.CodeGeneratorConstants.KOTLIN_SOURCE_FOLDER_NAME
import ru.hh.android.plugin.CodeGeneratorConstants.MAIN_SOURCE_SET_FOLDER_NAME
import ru.hh.android.plugin.CodeGeneratorConstants.SRC_FOLDER_NAME
import ru.hh.android.plugin.actions.modules.copy_module.exceptions.CopyModuleActionException
import ru.hh.android.plugin.actions.modules.copy_module.extensions.moduleMainSourceSetPsiDirectory
import ru.hh.android.plugin.actions.modules.copy_module.extensions.moduleToCopy
import ru.hh.android.plugin.actions.modules.copy_module.extensions.project
import ru.hh.android.plugin.actions.modules.copy_module.model.CopyModuleActionData
import ru.hh.android.plugin.actions.modules.copy_module.model.NewModuleDirectoriesStructure
import ru.hh.android.plugin.actions.modules.copy_module.model.NewModulePackagesInfo
import ru.hh.android.plugin.actions.modules.copy_module.model.NewModuleParams
import ru.hh.android.plugin.actions.modules.copy_module.view.CopyAndroidModuleActionDialog
import ru.hh.android.plugin.core.model.psi.GradleDependency
import ru.hh.android.plugin.core.model.psi.GradleDependencyMode
import ru.hh.android.plugin.core.model.psi.GradleDependencyType
import ru.hh.android.plugin.extensions.*
import ru.hh.android.plugin.services.code_generator.BuildGradleModificationService
import ru.hh.android.plugin.services.code_generator.SettingsGradleModificationService
import ru.hh.android.plugin.utils.PluginBundle.message
import ru.hh.android.plugin.utils.logDebug
import ru.hh.android.plugin.utils.logError
import ru.hh.android.plugin.utils.logInfo
import kotlin.system.measureTimeMillis


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
        e.androidFacet?.let { androidFacet ->
            handleAction(CopyModuleActionData(e, androidFacet))
        }
    }


    private fun handleAction(actionData: CopyModuleActionData) {
        val project = actionData.project
        val dialog = CopyAndroidModuleActionDialog(project, actionData.moduleToCopy.name)
        dialog.show()

        if (dialog.isOK.not()) {
            project.logError(message("geminio.notifications.copy_module.cancel"))
            return
        }

        val newModuleParams = NewModuleParams(
            newModuleName = dialog.getModuleName(),
            newPackageName = dialog.getPackageName(),
            appModule = dialog.getSelectedModule(),
            moduleToCopyFacet = actionData.androidFacet
        )

        if (newModuleParams.isValid()) {
            executeCommand {
                runWriteAction {
                    copyModule(newModuleParams)
                    project.service<SettingsGradleModificationService>()
                        .addGradleModuleDescription(
                            project = project,
                            moduleName = newModuleParams.newModuleName,
                            moduleRelativePath = "${newModuleParams.moduleToCopy.relativePathToParent}/${newModuleParams.newModuleName}"
                        )
                    project.service<BuildGradleModificationService>()
                        .addGradleDependenciesIntoModule(
                            module = newModuleParams.appModule,
                            gradleDependencies = listOf(
                                GradleDependency(
                                    text = newModuleParams.newModuleName,
                                    type = GradleDependencyType.MODULE,
                                    mode = GradleDependencyMode.IMPLEMENTATION
                                )
                            )
                        )

                    SyncProjectAction().actionPerformed(actionData.actionEvent)

                    project.logInfo(message("geminio.notifications.copy_module.success.0", newModuleParams.moduleToCopy.name))
                }
            }
        }
    }

    private fun copyModule(params: NewModuleParams) {
        val project = params.project

        val moduleParentPsiDirectory = params.moduleToCopy.moduleParentPsiDirectory ?: return
        val newModuleRootPsiDirectory = moduleParentPsiDirectory.createSubdirectory(params.newModuleName)
        project.logDebug("Parent directory for new module created [withName: ${params.newModuleName}]")

        copyFilesFromRootDirectory(params, newModuleRootPsiDirectory)
        val dirsStructure = createDirectoriesStructure(params, newModuleRootPsiDirectory)
        val mainPackagesInfo = getMainPackagesInfo(dirsStructure, params)
        copyAndroidManifestFile(mainPackagesInfo, params.moduleToCopy.name)
        copyFilesFromMainPackage(mainPackagesInfo)

        project.logInfo(message("geminio.notifications.copy_module.success.0", params.moduleToCopy.name))
    }

    private fun copyFilesFromRootDirectory(params: NewModuleParams, newModulePsiDirectory: PsiDirectory) {
        val project = params.project

        project.logDebug("Start copying files from module from root directory")
        val parentFilesCopyTime = measureTimeMillis {
            params.moduleToCopy
                .rootPsiDirectory
                ?.files
                ?.filter { it !is PsiPlainTextFile }
                ?.map { psiFile ->
                    project.logDebug("\tFind ${psiFile.name}, start copying...")
                    psiFile.copyFile().also { newPsiFile ->
                        if (newPsiFile.name == BuildGradleModificationService.BUILD_GRADLE_FILENAME) {
                            project.logDebug("\tFind build.gradle file, need modification of dependencies block")
                            project.service<BuildGradleModificationService>()
                                .addGradleDependenciesIntoBuildGradleFile(
                                    psiFile = newPsiFile,
                                    gradleDependencies = listOf(
                                        GradleDependency(
                                            text = params.moduleToCopy.name,
                                            type = GradleDependencyType.MODULE,
                                            mode = GradleDependencyMode.IMPLEMENTATION
                                        )
                                    )
                                )
                        }
                    }
                }
                ?.forEach { newModulePsiDirectory.add(it) }
        }
        project.logDebug("Successfully copied root directory files [time: $parentFilesCopyTime ms]")
    }

    private fun createDirectoriesStructure(params: NewModuleParams, newModuleRootPsiDirectory: PsiDirectory): NewModuleDirectoriesStructure {
        val moduleMainSourceSetPsiDirectory = params.moduleMainSourceSetPsiDirectory
            ?: throw CopyModuleActionException(
                message("geminio.errors.copy_module.cant_find_main_source_set.0", params.moduleToCopy.name)
            )
        val (moduleJavaSourcePsiDirectory, isSourceDirJava) =
            moduleMainSourceSetPsiDirectory.findSubdirectory(JAVA_SOURCE_FOLDER_NAME)?.let { Pair(it, true) }
                ?: moduleMainSourceSetPsiDirectory.findSubdirectory(KOTLIN_SOURCE_FOLDER_NAME)?.let { Pair(it, false) }
                ?: throw CopyModuleActionException(
                    message("geminio.errors.copy_module.cant_find_java_source.0",
                        params.moduleToCopy.name)
                )

        val newModuleSrcFolder = newModuleRootPsiDirectory.createSubdirectory(SRC_FOLDER_NAME)
        val newModuleMainFolder = newModuleSrcFolder.createSubdirectory(MAIN_SOURCE_SET_FOLDER_NAME)
        val newModuleJavaSourcePsiDirectory = when (isSourceDirJava) {
            true -> newModuleMainFolder.createSubdirectory(JAVA_SOURCE_FOLDER_NAME)
            else -> newModuleMainFolder.createSubdirectory(KOTLIN_SOURCE_FOLDER_NAME)
        }

        return NewModuleDirectoriesStructure(
            moduleToCopyMainSourceSetPsiDirectory = moduleMainSourceSetPsiDirectory,
            moduleToCopyJavaSourcePsiDirectory = moduleJavaSourcePsiDirectory,
            newModuleMainSourceSetPsiDirectory = newModuleMainFolder,
            newModuleJavaSourcePsiDirectory = newModuleJavaSourcePsiDirectory
        )
    }

    private fun getMainPackagesInfo(dirsStructure: NewModuleDirectoriesStructure, params: NewModuleParams): NewModulePackagesInfo {
        // Move down to the packages
        return with(dirsStructure) {
            NewModulePackagesInfo(
                moduleToCopyPackageName = params.moduleToCopyFacet.packageName,
                moduleToCopyMainPackagePsiDirectory = moduleToCopyJavaSourcePsiDirectory.findSubdirectoryByPackageName(
                    moduleName = params.moduleToCopy.name,
                    packageName = params.moduleToCopyFacet.packageName
                ),
                moduleToCopyMainSourceSetPsiDirectory = dirsStructure.moduleToCopyMainSourceSetPsiDirectory,
                newModulePackageName = params.newPackageName,
                newModuleMainPackagePsiDirectory = newModuleJavaSourcePsiDirectory.createSubdirectoriesForPackageName(
                    params.newPackageName
                ),
                newModuleMainSourceSetPsiDirectory = dirsStructure.newModuleMainSourceSetPsiDirectory
            )
        }
    }

    private fun copyAndroidManifestFile(mainPackagesInfo: NewModulePackagesInfo, moduleName: String) {
        with(mainPackagesInfo) {
            val androidManifestPsiFile = moduleToCopyMainSourceSetPsiDirectory.findFile(ANDROID_MANIFEST_XML_FILE_NAME)
                ?: throw CopyModuleActionException(
                    message("geminio.errors.copy_module.cant_find_android_manifest.0", moduleName))

            val newAndroidManifestPsiFile = androidManifestPsiFile.copyFile(
                textModification = { text ->
                    text.replace(moduleToCopyPackageName, newModulePackageName)
                }
            )
            newModuleMainSourceSetPsiDirectory.add(newAndroidManifestPsiFile)
        }
    }

    private fun copyFilesFromMainPackage(mainPackagesInfo: NewModulePackagesInfo) {
        with(mainPackagesInfo) {
            val copyMainPackageTime = measureTimeMillis {
                moduleToCopyMainPackagePsiDirectory.copyInto(
                    another = newModuleMainPackagePsiDirectory,
                    textTransformation = { text ->
                        text.replace(moduleToCopyPackageName, newModulePackageName)
                    }
                )
            }

            val project = moduleToCopyMainPackagePsiDirectory.project
            project.logDebug("Success copying [time: $copyMainPackageTime ms]")
        }
    }

    private fun NewModuleParams.isValid(): Boolean {
        project.logDebug("New module name: $newModuleName, package name: $newPackageName")
        project.logDebug("moduleParentPsiDirectory.path: ${moduleToCopy.moduleParentPsiDirectory?.virtualFile?.path}")

        val parentFolder = moduleToCopy.moduleParentPsiDirectory
        when {
            parentFolder == null -> {
                project.logError(message("geminio.notifications.copy_module.no_parent_folder.0", moduleToCopy.name))
                return false
            }

            parentFolder.canCreateSubdirectory(newModuleName).not() -> {
                project.logError(message("geminio.notifications.copy_module.cant_create_module_folder.0", newModuleName))
                return false
            }
        }

        return true
    }

}