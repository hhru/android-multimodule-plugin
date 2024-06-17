package ru.hh.android.plugin.actions.modules.copy_module

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.executeCommand
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiPlainTextFile
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
import ru.hh.android.plugin.extensions.androidFacet
import ru.hh.android.plugin.extensions.canCreateSubdirectory
import ru.hh.android.plugin.extensions.copyFile
import ru.hh.android.plugin.extensions.copyInto
import ru.hh.android.plugin.extensions.createSubdirectoriesForPackageName
import ru.hh.android.plugin.extensions.findSubdirectoryByPackageName
import ru.hh.android.plugin.extensions.moduleParentPsiDirectory
import ru.hh.android.plugin.extensions.packageName
import ru.hh.android.plugin.extensions.relativePathToParent
import ru.hh.android.plugin.extensions.rootPsiDirectory
import ru.hh.plugins.code_modification.BuildGradleModificationService
import ru.hh.plugins.code_modification.SettingsGradleModificationService
import ru.hh.plugins.dialog.sync.showSyncQuestionDialog
import ru.hh.plugins.extensions.openapi.isAndroidLibraryModule
import ru.hh.plugins.logger.HHLogger
import ru.hh.plugins.logger.HHNotifications
import ru.hh.plugins.models.gradle.BuildGradleDependency
import ru.hh.plugins.models.gradle.BuildGradleDependencyConfiguration
import kotlin.system.measureTimeMillis

/**
 * Action for copy module.
 */
class CopyAndroidModuleAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        super.update(e)

        e.presentation.isEnabled = when {
            e.androidFacet?.module?.isAndroidLibraryModule() == false -> false
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
            HHNotifications.error("Module copying cancelled")
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
                    SettingsGradleModificationService.getInstance(project)
                        .addGradleModuleDescription(
                            moduleName = newModuleParams.newModuleName,
                            moduleRelativePath = "${newModuleParams.moduleToCopy.relativePathToParent}/${newModuleParams.newModuleName}"
                        )
                    BuildGradleModificationService.getInstance(project)
                        .addDepsIntoModule(
                            module = newModuleParams.appModule,
                            gradleDependencies = listOf(
                                BuildGradleDependency.Project(
                                    configuration = BuildGradleDependencyConfiguration.IMPLEMENTATION,
                                    value = newModuleParams.newModuleName
                                )
                            )
                        )

                    project.showSyncQuestionDialog(syncPerformedActionEvent = actionData.actionEvent)
                    HHNotifications.info("Module \"${newModuleParams.moduleToCopy.name}\" successfully copied!")
                }
            }
        }
    }

    private fun copyModule(params: NewModuleParams) {
        val moduleParentPsiDirectory = params.moduleToCopy.moduleParentPsiDirectory ?: return
        val newModuleRootPsiDirectory = moduleParentPsiDirectory.createSubdirectory(params.newModuleName)
        HHLogger.d("Parent directory for new module created [withName: ${params.newModuleName}]")

        copyFilesFromRootDirectory(params, newModuleRootPsiDirectory)
        val dirsStructure = createDirectoriesStructure(params, newModuleRootPsiDirectory)
        val mainPackagesInfo = getMainPackagesInfo(dirsStructure, params)
        copyAndroidManifestFile(mainPackagesInfo, params.moduleToCopy.name)
        copyFilesFromMainPackage(mainPackagesInfo)
    }

    private fun copyFilesFromRootDirectory(params: NewModuleParams, newModulePsiDirectory: PsiDirectory) {
        val project = params.project

        HHLogger.d("Start copying files from module from root directory")
        val parentFilesCopyTime = measureTimeMillis {
            params.moduleToCopy
                .rootPsiDirectory
                ?.files
                ?.filter { it !is PsiPlainTextFile }
                ?.map { psiFile ->
                    HHLogger.d("\tFind ${psiFile.name}, start copying...")
                    psiFile.copyFile().also { newPsiFile ->
                        if (newPsiFile.name == "build.gradle") {
                            HHLogger.d("\tFind build.gradle file, need modification of dependencies block")
                            BuildGradleModificationService.getInstance(project)
                                .addDepsIntoFile(
                                    psiFile = newPsiFile,
                                    gradleDependencies = listOf(
                                        BuildGradleDependency.Project(
                                            configuration = BuildGradleDependencyConfiguration.IMPLEMENTATION,
                                            value = params.moduleToCopy.name
                                        )
                                    )
                                )
                        }
                    }
                }
                ?.forEach { newModulePsiDirectory.add(it) }
        }
        HHLogger.d("Successfully copied root directory files [time: $parentFilesCopyTime ms]")
    }

    private fun createDirectoriesStructure(
        params: NewModuleParams,
        newModuleRootPsiDirectory: PsiDirectory
    ): NewModuleDirectoriesStructure {
        val moduleMainSourceSetPsiDirectory = params.moduleMainSourceSetPsiDirectory
            ?: throw CopyModuleActionException(
                "Can't find main source set directory (/main) in copying module \"${params.moduleToCopy.name}\"! Make sure it exists."
            )
        val (moduleJavaSourcePsiDirectory, isSourceDirJava) =
            moduleMainSourceSetPsiDirectory.findSubdirectory(JAVA_SOURCE_FOLDER_NAME)?.let { Pair(it, true) }
                ?: moduleMainSourceSetPsiDirectory.findSubdirectory(KOTLIN_SOURCE_FOLDER_NAME)?.let { Pair(it, false) }
                ?: throw CopyModuleActionException(
                    "Can't find java sources directories (/java or /kotlin) in copying module \"${params.moduleToCopy.name}\"! Make sure it exists."
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

    private fun getMainPackagesInfo(
        dirsStructure: NewModuleDirectoriesStructure,
        params: NewModuleParams
    ): NewModulePackagesInfo {
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
                    "Can't find AndroidManifest.xml file in copying module \"${moduleName}\"! Make sure it exists."
                )

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

            HHLogger.d("Success copying [time: $copyMainPackageTime ms]")
        }
    }

    private fun NewModuleParams.isValid(): Boolean {
        HHLogger.d("New module name: $newModuleName, package name: $newPackageName")
        HHLogger.d("moduleParentPsiDirectory.path: ${moduleToCopy.moduleParentPsiDirectory?.virtualFile?.path}")

        val parentFolder = moduleToCopy.moduleParentPsiDirectory
        when {
            parentFolder == null -> {
                HHNotifications.error("No parent folder for module \"${moduleToCopy.name}\" found")
                return false
            }

            parentFolder.canCreateSubdirectory(newModuleName).not() -> {
                HHNotifications.error("Can't create new module folder (with name: \"${newModuleName}\")")
                return false
            }
        }

        return true
    }
}
