package ru.hh.android.plugin.services.code_generator

import com.android.tools.idea.templates.TemplateUtils
import com.intellij.openapi.command.executeCommand
import com.intellij.openapi.components.Service
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPsiFactory
import ru.hh.android.plugin.extensions.psi.kotlin.addImportPackages
import ru.hh.android.plugin.extensions.psi.kotlin.getBreakLineElement
import ru.hh.android.plugin.utils.reformatWithCodeStyle


@Service
class SerializedNameAnnotationsGeneratorService {

    companion object {
        private const val ANNOTATION_NAME = "SerializedName"
        private const val SERIALIZED_NAME_ANNOTATION_FQN = "com.google.gson.annotations.SerializedName"
    }


    fun addSerializedNameAnnotationsIntoClass(ktClass: KtClass) {
        require(ktClass.isData())

        val ktPsiFactory = KtPsiFactory(ktClass.project)

        executeCommand {
            runWriteAction {
                ktClass.primaryConstructorParameters.forEach { parameter ->
                    if (parameter.hasSerializedNameAnnotation().not()) {
                        val annotationEntry = ktPsiFactory.createAnnotationEntry(parameter.toSerializedNameAnnotationText())

                        parameter.addBefore(annotationEntry, parameter)
                        parameter.addBefore(ktPsiFactory.getBreakLineElement(), parameter)
                    }
                }

                ktClass.containingKtFile.addImportPackages(SERIALIZED_NAME_ANNOTATION_FQN)
                ktClass.containingKtFile.reformatWithCodeStyle()
            }
        }
    }


    private fun KtParameter.hasSerializedNameAnnotation(): Boolean {
        return findAnnotation(FqName(SERIALIZED_NAME_ANNOTATION_FQN)) != null
    }

    private fun KtParameter.toSerializedNameAnnotationText(): String {
        val parameterNameInSnakeCase = TemplateUtils.camelCaseToUnderlines(name)
        return "@$ANNOTATION_NAME(\"${parameterNameInSnakeCase}\")"
    }

}