package ru.hh.android.plugins.android_feature_module.build_tasks

import com.intellij.psi.search.FilenameIndex
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import ru.hh.android.plugins.android_feature_module.ProjectInfo
import ru.hh.android.plugins.android_feature_module.WriteActionsFactory
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig

class AddModuleIntoDependenciesBlockTask
    : BuildTask("Change dependencies config in application module.") {

    companion object {
        private const val BUILD_GRADLE_FILE_NAME = "build.gradle"

        private const val DEPENDENCIES_BLOCK_NAME = "dependencies"
    }

    override fun internalPerformAction(config: BuildTasksConfig) {
        config.applicationsModules.forEach { app ->
            val appModule = app.gradleModule

            val buildGradlePsiFile = FilenameIndex.getFilesByName(
                    ProjectInfo.getProject(),
                    BUILD_GRADLE_FILE_NAME,
                    appModule.moduleContentScope
            ).first()

            val dependenciesClosableBlock = buildGradlePsiFile.originalFile
                    .children
                    .first { it.text.startsWith(DEPENDENCIES_BLOCK_NAME) }
                    .lastChild

            WriteActionsFactory.runWriteAction(
                    project = buildGradlePsiFile.project,
                    actionDescription = "Change dependencies block in application's build.gradle",
                    action = Runnable {
                        val factory = GroovyPsiElementFactory.getInstance(buildGradlePsiFile.project)

                        dependenciesClosableBlock.addBefore(
                                getDependencyExpression(factory, config),
                                dependenciesClosableBlock.lastChild
                        )
                        dependenciesClosableBlock.addBefore(
                                factory.createLineTerminator(1),
                                dependenciesClosableBlock.lastChild
                        )
                    }
            )
        }
    }

    private fun getDependencyExpression(factory: GroovyPsiElementFactory, config: BuildTasksConfig): GrExpression {
        return factory.createExpressionFromText("implementation project(':${config.moduleName}')")
    }

}