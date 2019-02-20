package ru.hh.android.plugin.feature_module.component.build_module.task

import com.intellij.openapi.project.Project
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.extensions.getRootModulePath
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import java.io.File


@Deprecated("")
class DirsStructureCreationTask(
        project: Project,
        logger: PluginLogger
) : BuildModuleTask("Create dirs structure", project, logger) {

    override fun execute(config: CreateModuleConfig) {
        val mainParamsHolder = config.mainParams
        val rootModuleDirPath = project.getRootModulePath()

        val baseModulePath = mainParamsHolder.moduleType.typeRootFolder
        val modulePath = "$rootModuleDirPath$baseModulePath/${mainParamsHolder.moduleName}".replace("//", "/")
        val packagePath = "$modulePath/src/main/java/${mainParamsHolder.slashedPackageName}"

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
                logger.log("\tCreate dir: '$dirPath'.")
            }
        }
    }

}