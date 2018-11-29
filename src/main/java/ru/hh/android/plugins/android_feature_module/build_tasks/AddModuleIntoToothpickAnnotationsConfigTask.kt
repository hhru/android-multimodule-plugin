package ru.hh.android.plugins.android_feature_module.build_tasks

import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.GrListOrMap
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrAssignmentExpression
import ru.hh.android.plugins.android_feature_module.ProjectInfo
import ru.hh.android.plugins.android_feature_module.WriteActionsFactory
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig


class AddModuleIntoToothpickAnnotationsConfigTask
    : BuildTask("Change Toothpick annotation processing config in application module.") {

    companion object {
        private const val BUILD_GRADLE_FILE_NAME = "build.gradle"

        private const val ARGUMENTS_ASSIGNMENT_NAME = "arguments"
        private const val TOOTHPICK_REGISTRY_CHILDREN_PACKAGE_NAMES_OPTION_ITEM_NAME =
                "toothpick_registry_children_package_names"
    }


    override fun internalPerformAction(config: BuildTasksConfig) {
        config.applicationsModules.forEach { app ->
            val appModule = app.gradleModule

            val buildGradlePsiFile = FilenameIndex.getFilesByName(
                    ProjectInfo.getProject(),
                    BUILD_GRADLE_FILE_NAME,
                    appModule.moduleContentScope
            ).first()

            val toothpickRegistryChildrenPackageNamesListPsiElement = buildGradlePsiFile.originalFile
                    .collectDescendantsOfType<GrAssignmentExpression>().first { it.text.startsWith(ARGUMENTS_ASSIGNMENT_NAME) }
                    .lastChild
                    .firstChildWithStartText(TOOTHPICK_REGISTRY_CHILDREN_PACKAGE_NAMES_OPTION_ITEM_NAME)
                    .collectDescendantsOfType<GrListOrMap>()

            WriteActionsFactory.runWriteAction(
                    project = buildGradlePsiFile.project,
                    actionDescription = "Change toothpick annotations block in application's build.gradle",
                    action = Runnable {
                        val factory = GroovyPsiElementFactory.getInstance(buildGradlePsiFile.project)
                        val newArgumentItem = factory.createStringLiteralForReference(config.packageName)
                        toothpickRegistryChildrenPackageNamesListPsiElement.forEach { it.add(newArgumentItem) }
                    }
            )
        }
    }

    private fun PsiElement.firstChildWithStartText(startText: String): PsiElement {
        return children.first { it.text.startsWith(startText) }
    }

}