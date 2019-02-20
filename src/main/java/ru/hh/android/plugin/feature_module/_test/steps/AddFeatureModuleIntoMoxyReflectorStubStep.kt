package ru.hh.android.plugin.feature_module._test.steps

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.asJava.elements.KtLightAnnotationForSourceEntry
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.plugins.groovy.lang.psi.util.childrenOfType
import ru.hh.android.plugin.feature_module.extensions.findClassesAnnotatedWith
import ru.hh.android.plugin.feature_module.model.CreateModuleConfig


class AddFeatureModuleIntoMoxyReflectorStubStep {

    companion object {
        private const val SHORT_MOXY_ANNOTATION_NAME = "RegisterMoxyReflectorPackages"
        private const val FULL_QUALIFIED_MOXY_ANNOTATION_NAME = "com.arellomobile.mvp.RegisterMoxyReflectorPackages"
        private const val ATTRIBUTE_VALUE_NAME = "value"
    }


    fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyMoxyReflectorStub(appModuleItem.gradleModule, config)
        }
    }


    private fun modifyMoxyReflectorStub(module: Module, config: CreateModuleConfig) {
        val annotatedPsiClass = module.findClassesAnnotatedWith(FULL_QUALIFIED_MOXY_ANNOTATION_NAME)?.first() ?: return

        val factory = KtPsiFactory(module.project)

        val annotationPsiElement = (annotatedPsiClass.annotations.first() as KtLightAnnotationForSourceEntry).kotlinOrigin
        val values = annotationPsiElement
                .collectDescendantsOfType<KtValueArgumentList>().first()
                .collectDescendantsOfType<KtValueArgument>()
                .mapTo(mutableListOf()) { it.text }

        values += "\"${config.mainParams.packageName}\""

        val newAnnotation = factory.createAnnotationEntry("@$SHORT_MOXY_ANNOTATION_NAME(\n${values.joinToString(separator = ",\n")}\n)")
        val replacedElement = annotationPsiElement.replace(newAnnotation)
        CodeStyleManager.getInstance(module.project).reformat(replacedElement)
    }

}