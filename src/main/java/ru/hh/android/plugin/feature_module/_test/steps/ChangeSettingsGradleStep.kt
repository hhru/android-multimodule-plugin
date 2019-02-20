package ru.hh.android.plugin.feature_module._test.steps

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.refactoring.toPsiDirectory
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.android.plugin.feature_module.extensions.createBreakLineElement
import ru.hh.android.plugin.feature_module.extensions.createProjectDirPathExpression
import ru.hh.android.plugin.feature_module.extensions.generateIncludeExpression
import ru.hh.android.plugin.feature_module.extensions.replaceMultipleSplashes
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig
import ru.hh.android.plugin.feature_module.model.enums.FeatureModuleType

class ChangeSettingsGradleStep {

    fun change(project: Project, config: CreateModuleConfig) {
        val settingsGradlePsiFile = project.baseDir.toPsiDirectory(project)?.findFile("settings.gradle") ?: return

        val projectPath = project.basePath
        val modulePath = "$projectPath${config.mainParams.moduleType.typeRootFolder}/${config.mainParams.moduleName}".replaceMultipleSplashes()

        val factory = GroovyPsiElementFactory.getInstance(settingsGradlePsiFile.project)

        with(settingsGradlePsiFile) {
            add(factory.createBreakLineElement())
            add(factory.generateIncludeExpression(config.mainParams.moduleName))
            add(factory.createBreakLineElement())

            if (config.mainParams.moduleType != FeatureModuleType.STANDALONE) {
                add(factory.createProjectDirPathExpression(config.mainParams.moduleName, modulePath))
                add(factory.createBreakLineElement())
            }
        }
    }

}