package ru.hh.android.plugins.android_feature_module.build_tasks

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiManager
import com.intellij.psi.search.searches.AnnotatedMembersSearch
import com.intellij.psi.util.ClassUtil
import org.jetbrains.plugins.groovy.lang.psi.util.childrenOfType
import ru.hh.android.plugins.android_feature_module.ProjectInfo
import ru.hh.android.plugins.android_feature_module.WriteActionsFactory
import ru.hh.android.plugins.android_feature_module.models.BuildTasksConfig


class ChangeMoxyReflectorStubTask
    : BuildTask("Add module package name into MoxyReflector class.") {

    companion object {
        private const val FULL_QUALIFIED_MOXY_ANNOTATION_NAME = "com.arellomobile.mvp.RegisterMoxyReflectorPackages"
        private const val ATTRIBUTE_VALUE_NAME = "value"
    }

    override fun internalPerformAction(config: BuildTasksConfig) {
        config.applicationsModules.forEach { app ->
            val appModule = app.gradleModule

            val psiManager = PsiManager.getInstance(ProjectInfo.getProject())
            val annotatedClass = ClassUtil.findPsiClass(psiManager, FULL_QUALIFIED_MOXY_ANNOTATION_NAME)?.let {
                AnnotatedMembersSearch.search(it, appModule.moduleContentScope).findFirst()
            }

            annotatedClass?.let { annotatedPsiClass ->
                WriteActionsFactory.runWriteAction(
                        project = ProjectInfo.getProject(),
                        actionDescription = "Change Moxy annotation",
                        action = Runnable {
                            val factory = JavaPsiFacade.getInstance(ProjectInfo.getProject()).elementFactory

                            val annotationValues = annotatedPsiClass.annotations
                                    .first()
                                    .findAttributeValue(ATTRIBUTE_VALUE_NAME)
                                    ?.childrenOfType<PsiLiteralExpression>()
                                    ?.mapTo(mutableListOf()) { it.text }

                            annotationValues?.let { notNullValues ->
                                notNullValues += "\"${config.packageName}\""

                                val joinedValues = notNullValues.joinToString(separator = ",\n")
                                val newAnnotation = factory.createAnnotationFromText(
                                        "@$FULL_QUALIFIED_MOXY_ANNOTATION_NAME({\n$joinedValues\n})",
                                        null
                                )

                                annotatedPsiClass.annotations.forEach { it.replace(newAnnotation) }
                            }
                        }
                )
            }
        }
    }
}