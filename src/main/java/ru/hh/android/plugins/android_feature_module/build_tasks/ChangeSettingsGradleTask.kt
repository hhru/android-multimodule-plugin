package ru.hh.android.plugins.android_feature_module.build_tasks

import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import ru.hh.android.plugins.android_feature_module.ProjectInfo
import ru.hh.android.plugins.android_feature_module.WriteActionsFactory
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig
import ru.hh.android.plugins.android_feature_module.models.enums.AndroidFeatureModuleType


class ChangeSettingsGradleTask
    : BuildTask("Changing settings.gradle file to enable module") {

    companion object {
        private const val BREAK_LINE_SYMBOL = "\n"
        private const val SETTINGS_GRADLE_FILE_NAME = "settings.gradle"
    }


    override fun internalPerformAction(config: BuildTasksConfig) {
        val rootModule = ProjectInfo.getRootModule(ProjectInfo.getProject())

        val settingsGradleFile = FilenameIndex.getFilesByName(
                rootModule.project,
                SETTINGS_GRADLE_FILE_NAME,
                rootModule.moduleContentScope
        ).first()

        WriteActionsFactory.runWriteAction(
                project = rootModule.project,
                actionDescription = "Add module path into settings.gradle",
                action = Runnable {
                    val factory = GroovyPsiElementFactory.getInstance(settingsGradleFile.project)

                    val baseModuleDirPath = config.moduleType.typeRootFolder
                    val folderPath = ".$baseModuleDirPath/${config.moduleName}".replace("//", "/")

                    with(settingsGradleFile) {
                        add(factory.createBreakLineElement())
                        add(factory.generateIncludeExpression(config.moduleName))
                        add(factory.createBreakLineElement())

                        if (config.moduleType != AndroidFeatureModuleType.STANDALONE) {
                            add(factory.createProjectDirPathExpression(config.moduleName, folderPath))
                            add(factory.createBreakLineElement())
                        }
                    }
                }
        )
    }


    private fun GroovyPsiElementFactory.createBreakLineElement(): PsiElement {
        return createLineTerminator(BREAK_LINE_SYMBOL)
    }

    private fun GroovyPsiElementFactory.generateIncludeExpression(moduleName: String): GrExpression {
        return createExpressionFromText("include ':$moduleName'")
    }

    private fun GroovyPsiElementFactory.createProjectDirPathExpression(
            moduleName: String,
            folderPath: String
    ): GrExpression {
        return createExpressionFromText("project(':$moduleName').projectDir = new File(settingsDir, '$folderPath')")
    }

}