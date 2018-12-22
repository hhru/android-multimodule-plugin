package ru.hh.android.plugin.feature_module.component.build_module.task

import com.intellij.openapi.project.Project
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.extensions.*
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.enums.FeatureModuleType


class SettingsGradleFileModificationTask(
        project: Project,
        logger: PluginLogger
) : BuildModuleTask("Change settings.gradle file", project, logger) {

    companion object {
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"
    }


    override fun execute(config: CreateModuleConfig) {
        val rootModule = project.getRootModule()
        val settingsGradlePsiFile = rootModule.findPsiFileByName(SETTINGS_GRADLE_FILE_NAME)

        if (settingsGradlePsiFile == null) {
            logger.log("There are no $SETTINGS_GRADLE_FILE_NAME in root module!")
            return
        }

        val factory = GroovyPsiElementFactory.getInstance(settingsGradlePsiFile.project)
        val mainParamsHolder = config.mainParametersHolder

        val baseModuleDirPath = mainParamsHolder.moduleType.typeRootFolder
        val folderPath = ".$baseModuleDirPath/${mainParamsHolder.moduleName}".replaceMultipleSplashes()

        with(settingsGradlePsiFile) {
            add(factory.createBreakLineElement())
            add(factory.generateIncludeExpression(mainParamsHolder.moduleName))
            add(factory.createBreakLineElement())

            if (mainParamsHolder.moduleType != FeatureModuleType.STANDALONE) {
                add(factory.createProjectDirPathExpression(mainParamsHolder.moduleName, folderPath))
                add(factory.createBreakLineElement())
            }
        }
    }

}