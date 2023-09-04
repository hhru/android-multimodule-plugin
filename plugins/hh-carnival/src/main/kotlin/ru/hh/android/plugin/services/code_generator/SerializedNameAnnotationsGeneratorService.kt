package ru.hh.android.plugin.services.code_generator

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.codeinsight.utils.commitAndUnblockDocument
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.getValueParameterList
import ru.hh.android.plugin.utils.reformatWithCodeStyle
import ru.hh.plugins.extensions.EMPTY
import ru.hh.plugins.extensions.fromCamelCaseToUnderlines

@Service
class SerializedNameAnnotationsGeneratorService(
    private val project: Project
) {

    companion object {
        private const val COMMAND_NAME = "SerializedNameAnnotationsGenerator"

        private const val SERIALIZED_NAME_ANNOTATION_FQN = "com.google.gson.annotations.SerializedName"

        fun getInstance(project: Project): SerializedNameAnnotationsGeneratorService = project.service()
    }

    fun addSerializedNameAnnotationsIntoClass(ktClass: KtClass) {
        require(ktClass.isData())

        val ktPsiFactory = KtPsiFactory(project)

        project.executeWriteCommand(COMMAND_NAME) {
            var addAtLeastOneAnnotation = false
            ktClass.primaryConstructorParameters.forEach { parameter ->
                if (parameter.hasSerializedNameAnnotation().not()) {
                    val annotationEntry = ktPsiFactory.createAnnotationEntry(parameter.toSerializedNameAnnotationText())

                    ktClass.getValueParameterList()?.let { valueParameterList ->
                        valueParameterList.addBefore(annotationEntry, parameter)
                        valueParameterList.addBefore(ktPsiFactory.createNewLine(), parameter)
                    }

                    addAtLeastOneAnnotation = true
                }
            }

            if (addAtLeastOneAnnotation) {
                ktClass.containingKtFile.commitAndUnblockDocument()
                ShortenReferences.DEFAULT.process(ktClass)
                ktClass.containingKtFile.reformatWithCodeStyle()
            }
        }
    }

    private fun KtParameter.hasSerializedNameAnnotation(): Boolean {
        return findAnnotation(FqName(SERIALIZED_NAME_ANNOTATION_FQN)) != null
    }

    private fun KtParameter.toSerializedNameAnnotationText(): String {
        val parameterNameInSnakeCase = (name ?: String.EMPTY).fromCamelCaseToUnderlines()
        return "@$SERIALIZED_NAME_ANNOTATION_FQN(\"${parameterNameInSnakeCase}\")"
    }
}
