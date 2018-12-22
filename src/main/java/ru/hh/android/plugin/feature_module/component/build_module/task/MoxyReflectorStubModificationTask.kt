package ru.hh.android.plugin.feature_module.component.build_module.task

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiLiteralExpression
import org.jetbrains.plugins.groovy.lang.psi.util.childrenOfType
import ru.hh.android.plugin.feature_module.component.logger.PluginLogger
import ru.hh.android.plugin.feature_module.extensions.findClassesAnnotatedWith
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig


class MoxyReflectorStubModificationTask(
        project: Project,
        logger: PluginLogger
) : BuildModuleTask("Modify MoxyReflectorStub class", project, logger) {

    companion object {
        private const val FULL_QUALIFIED_MOXY_ANNOTATION_NAME = "com.arellomobile.mvp.RegisterMoxyReflectorPackages"
        private const val ATTRIBUTE_VALUE_NAME = "value"
    }


    override fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyMoxyReflectorStub(appModuleItem.gradleModule, config)
        }
    }


    private fun modifyMoxyReflectorStub(module: Module, config: CreateModuleConfig) {
        val annotatedPsiClass = module.findClassesAnnotatedWith(FULL_QUALIFIED_MOXY_ANNOTATION_NAME)?.first()

        if (annotatedPsiClass == null) {
            logger.log("There is no classes annotated with @$FULL_QUALIFIED_MOXY_ANNOTATION_NAME!")
            return
        }

        val factory = JavaPsiFacade.getInstance(module.project).elementFactory

        val annotationValues = annotatedPsiClass.annotations
                .first()
                .findAttributeValue(ATTRIBUTE_VALUE_NAME)
                ?.childrenOfType<PsiLiteralExpression>()
                ?.mapTo(mutableListOf()) { it.text }

        annotationValues?.let { notNullValues ->
            val packageName = config.mainParametersHolder.packageName

            notNullValues += "\"$packageName\""

            val joinedValues = notNullValues.joinToString(separator = ",\n")
            val newAnnotation = factory.createAnnotationFromText(
                    "@$FULL_QUALIFIED_MOXY_ANNOTATION_NAME({\n$joinedValues\n})",
                    null
            )

            annotatedPsiClass.annotations.forEach { it.replace(newAnnotation) }
        }
    }

}