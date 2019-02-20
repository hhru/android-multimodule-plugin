package ru.hh.android.plugin.feature_module.component.build_module.task

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.GrListOrMap
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrAssignmentExpression
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.extensions.findPsiFileByName
import ru.hh.android.plugin.feature_module.extensions.firstChildWithStartText
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig


class ToothpickAnnotationProcessingConfigModificationTask(
        project: Project,
        logger: PluginLogger
) : BuildModuleTask("Change annotationProcessor options for Toothpick", project, logger) {

    companion object {
        private const val BUILD_GRADLE_FILE_NAME = "build.gradle"

        private const val ARGUMENTS_ASSIGNMENT_NAME = "arguments"
        private const val TOOTHPICK_REGISTRY_CHILDREN_PACKAGE_NAMES_OPTION_ITEM_NAME =
                "toothpick_registry_children_package_names"
    }


    override fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyAnnotationProcessorOptions(appModuleItem.gradleModule, config)
        }
    }


    private fun modifyAnnotationProcessorOptions(module: Module, config: CreateModuleConfig) {
        val buildGradlePsiFile = module.findPsiFileByName(BUILD_GRADLE_FILE_NAME)

        if (buildGradlePsiFile == null) {
            logger.log("There is no $BUILD_GRADLE_FILE_NAME in ${module.name} module!")
            return
        }

        val toothpickRegistryChildrenPackageNamesListPsiElement = buildGradlePsiFile.originalFile
                .collectDescendantsOfType<GrAssignmentExpression>()
                .firstOrNull { it.text.startsWith(ARGUMENTS_ASSIGNMENT_NAME) }
                ?.lastChild
                ?.firstChildWithStartText(TOOTHPICK_REGISTRY_CHILDREN_PACKAGE_NAMES_OPTION_ITEM_NAME)
                ?.collectDescendantsOfType<GrListOrMap>()

        if (toothpickRegistryChildrenPackageNamesListPsiElement == null) {
            logger.log("There is no toothpick registry options in $BUILD_GRADLE_FILE_NAME!")
            return
        }

        val factory = GroovyPsiElementFactory.getInstance(buildGradlePsiFile.project)
        val packageName = config.mainParams.packageName

        val newArgumentItem = factory.createStringLiteralForReference(packageName)
        toothpickRegistryChildrenPackageNamesListPsiElement.forEach { it.add(newArgumentItem) }
    }


}