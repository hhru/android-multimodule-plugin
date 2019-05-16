package ru.hh.android.plugin.generator.steps

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.asJava.elements.KtLightAnnotationForSourceEntry
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import ru.hh.android.plugin.extensions.findAnnotationWithName
import ru.hh.android.plugin.extensions.findClassesAnnotatedWith
import ru.hh.android.plugin.model.CreateModuleConfig


class MoxyReflectorStubStep(
        private val project: Project
) {

    companion object {
        private const val SHORT_MOXY_ANNOTATION_NAME = "RegisterMoxyReflectorPackages"
        private const val FULL_QUALIFIED_MOXY_ANNOTATION_NAME = "com.arellomobile.mvp.RegisterMoxyReflectorPackages"
    }


    fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyMoxyReflectorStub(appModuleItem.gradleModule, config)
        }
        return
    }


    private fun modifyMoxyReflectorStub(module: Module, config: CreateModuleConfig) {
        val annotatedPsiClass = module.findClassesAnnotatedWith(FULL_QUALIFIED_MOXY_ANNOTATION_NAME)?.first() ?: return
        val moxyAnnotation = annotatedPsiClass.findAnnotationWithName(SHORT_MOXY_ANNOTATION_NAME) ?: return

        val kotlinAnnotationPsiElement = (moxyAnnotation as KtLightAnnotationForSourceEntry).kotlinOrigin
        val updatedValues = getUpdatedPackagesListFromValueAttribute(kotlinAnnotationPsiElement, config)

        val annotationWithUpdatedAttributesPsiElement = createAnnotationWithUpdatedValues(updatedValues)
        val replacedElement = kotlinAnnotationPsiElement.replace(annotationWithUpdatedAttributesPsiElement)

        CodeStyleManager.getInstance(module.project).reformat(replacedElement)
    }

    private fun getUpdatedPackagesListFromValueAttribute(
            annotationPsiElement: PsiElement,
            config: CreateModuleConfig
    ): List<String> {
        return annotationPsiElement
                .collectDescendantsOfType<KtValueArgumentList>()
                .first()
                .collectDescendantsOfType<KtValueArgument>()
                .mapTo(mutableListOf()) { it.text }
                .apply { this += "\"${config.mainParams.packageName}\"" }
    }

    private fun createAnnotationWithUpdatedValues(values: List<String>): PsiElement {
        val psiElementsFactory = KtPsiFactory(project)

        return psiElementsFactory.createAnnotationEntry(
                "@$SHORT_MOXY_ANNOTATION_NAME(\n${values.joinToString(separator = ",\n")}\n)"
        )
    }


}