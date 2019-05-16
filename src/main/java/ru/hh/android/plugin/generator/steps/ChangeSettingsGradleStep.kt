package ru.hh.android.plugin.generator.steps

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import org.jetbrains.kotlin.idea.refactoring.toPsiDirectory
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.android.plugin.extensions.createBreakLineElement
import ru.hh.android.plugin.extensions.createProjectDirPathExpression
import ru.hh.android.plugin.extensions.generateIncludeExpression
import ru.hh.android.plugin.extensions.replaceMultipleSplashes
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.model.enums.FeatureModuleType


class ChangeSettingsGradleStep {

    companion object {
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"
    }


    fun execute(project: Project, config: CreateModuleConfig) {
        val settingsGradlePsiFile = project.guessProjectDir()
                ?.toPsiDirectory(project)?.findFile(SETTINGS_GRADLE_FILE_NAME)
                ?: return

        val modulePath = "./${config.mainParams.moduleType.typeRootFolder}/${config.mainParams.moduleName}"
                .replaceMultipleSplashes()

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