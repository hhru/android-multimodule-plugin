package ru.hh.android.plugins.android_feature_module.build_tasks

import ru.hh.android.plugins.android_feature_module.ProjectInfo
import ru.hh.android.plugins.android_feature_module.WriteActionsFactory
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig
import java.io.File


class DirsStructureCreationTask
    : BuildTask("Module directories structure creation") {

    override fun internalPerformAction(config: BuildTasksConfig) {
        val rootModuleDirPath = ProjectInfo.getRootModuleDirPath()

        val baseModulePath = config.moduleType.typeRootFolder
        val modulePath = "$rootModuleDirPath$baseModulePath/${config.moduleName}".replace("//", "/")
        val packagePath = "$modulePath/src/main/java/${config.slashedPackageName}"

        WriteActionsFactory.runWriteAction(ProjectInfo.getProject(), "Create directories", Runnable {
            listOf(
                    packagePath,
                    "$packagePath/adapter",
                    "$packagePath/adapter/delegate",
                    "$packagePath/adapter/item",
                    "$packagePath/data_source",
                    "$packagePath/di",
                    "$packagePath/extensions",
                    "$packagePath/interactor",
                    "$packagePath/model",
                    "$packagePath/model/network",
                    "$packagePath/model/domain",
                    "$packagePath/model/database",
                    "$packagePath/model/presentation",
                    "$packagePath/presenter",
                    "$packagePath/repository",
                    "$packagePath/view",
                    "$packagePath/screen",
                    "$packagePath/custom_view"
            ).forEach { dirPath ->
                if (File(dirPath).mkdirs()) {
                    println("\tCreate dir: '$dirPath'.")
                }
            }
        })
    }

}