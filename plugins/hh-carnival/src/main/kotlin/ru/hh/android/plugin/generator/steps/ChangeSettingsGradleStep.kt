package ru.hh.android.plugin.generator.steps

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import ru.hh.android.plugin.model.CreateModuleConfig
import ru.hh.android.plugin.model.enums.FeatureModuleType
import ru.hh.plugins.code_modification.extensions.psi.getBreakLineElement
import ru.hh.plugins.code_modification.extensions.psi.getIncludeModuleExpressionElement
import ru.hh.plugins.code_modification.extensions.psi.getIncludeModuleRelativePathSetupElement


class ChangeSettingsGradleStep {

    companion object {
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"
    }


    fun execute(project: Project, config: CreateModuleConfig) {
        val settingsGradlePsiFile = project.guessProjectDir()
            ?.toPsiDirectory(project)?.findFile(SETTINGS_GRADLE_FILE_NAME)
            ?: return

        val modulePath = config.params.settingsGradleModulePath
        val factory = GroovyPsiElementFactory.getInstance(settingsGradlePsiFile.project)

        with(settingsGradlePsiFile) {
            add(factory.getBreakLineElement())
            add(factory.getIncludeModuleExpressionElement(config.params.moduleName))
            add(factory.getBreakLineElement())

            if (config.params.moduleType != FeatureModuleType.STANDALONE) {
                add(factory.getIncludeModuleRelativePathSetupElement(config.params.moduleName, modulePath))
                add(factory.getBreakLineElement())
            }
        }
    }

}