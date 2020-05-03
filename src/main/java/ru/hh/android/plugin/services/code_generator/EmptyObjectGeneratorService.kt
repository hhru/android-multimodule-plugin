package ru.hh.android.plugin.services.code_generator

import com.intellij.openapi.command.executeCommand
import com.intellij.openapi.components.Service
import org.jetbrains.kotlin.idea.core.getOrCreateCompanionObject
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.getOrCreateBody
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import ru.hh.android.plugin.CodeGeneratorConstants.EMPTY_OBJECT_PROPERTY_NAME
import ru.hh.android.plugin.utils.reformatWithCodeStyle


@Service
class EmptyObjectGeneratorService {

    fun addEmptyObjectIntoKtClass(ktClass: KtClass) {
        val project = ktClass.project

        if (ktClass.isData().not()) {
            throw IllegalArgumentException("${ktClass.name} is not data class!")
        }

        val newEmptyObjectDeclaration = """
        val $EMPTY_OBJECT_PROPERTY_NAME = ${ktClass.getEmptyObjectDeclaration()}
        """
        val ktPsiFactory = KtPsiFactory(project)
        val propertyDeclaration = ktPsiFactory.createProperty(newEmptyObjectDeclaration)

        executeCommand {
            runWriteAction {
                val companionObject = ktClass.getOrCreateCompanionObject()
                if (companionObject.findPropertyByName(EMPTY_OBJECT_PROPERTY_NAME) != null) {
                    throw IllegalArgumentException("${ktClass.name} already has EMPTY object property!")
                }

                val companionObjectBody = companionObject.getOrCreateBody()
                companionObjectBody.addBefore(propertyDeclaration, companionObjectBody.rBrace)

                ktClass.reformatWithCodeStyle()
            }
        }
    }


    private fun KtClass.getEmptyObjectDeclaration(): String {
        val emptyProperties = primaryConstructorParameters.joinToString(separator = ",\n") { parameter ->
            "${parameter.name} = ${parameter.getEmptyObjectValue()}"
        }
        return "${name}(\n$emptyProperties\n)"
    }

    private fun KtParameter.getEmptyObjectValue(): String {
        val parameterType = type()

        return when {
            parameterType == null || parameterType.isMarkedNullable -> {
                "null"
            }

            else -> {
                val identifier = parameterType.nameIfStandardType?.identifier
                when {
                    identifier.isNullOrBlank() -> "null"
                    identifier == "String" -> "\"\""
                    identifier == "Byte" -> "0"
                    identifier == "Int" -> "0"
                    identifier == "Short" -> "0"
                    identifier == "Long" -> "0L"
                    identifier == "Float" -> "0f"
                    identifier == "Double" -> "0.0"
                    identifier == "Boolean" -> "false"
                    identifier.startsWith("List") -> "emptyList()"
                    identifier.startsWith("Array") -> "emptyArray()"
                    identifier.startsWith("Set") -> "emptySet()"
                    identifier.startsWith("Map") -> "emptyMap()"
                    else -> "null"
                }
            }
        }
    }

}