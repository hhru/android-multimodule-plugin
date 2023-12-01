package ru.hh.android.plugin.services.code_generator

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.idea.core.getOrCreateCompanionObject
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.getOrCreateBody
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import ru.hh.android.plugin.CodeGeneratorConstants.EMPTY_OBJECT_PROPERTY_NAME
import ru.hh.android.plugin.extensions.psi.kotlin.addImportPackages
import ru.hh.android.plugin.utils.reformatWithCodeStyle

@Service
class EmptyObjectGeneratorService(
    private val project: Project
) {

    companion object {
        private const val COMMAND_NAME = "EmtpyObjectGenerator"

        private const val STRING_PARAMETER_TYPE_NAME = "String"
        private const val EMPTY_STRING_PROPERTY_FQN = "ru.hh.shared.core.utils.EMPTY"

        fun getInstance(project: Project): EmptyObjectGeneratorService = project.service()
    }

    fun addEmptyObjectIntoKtClass(ktClass: KtClass) {
        require(ktClass.isData())
        require(ktClass.companionObjects.firstOrNull()?.findPropertyByName(EMPTY_OBJECT_PROPERTY_NAME) == null)

        val ktPsiFactory = KtPsiFactory(project)

        val newEmptyObjectDeclaration = ktClass.getEmptyObjectPropertyDeclaration()
        val propertyDeclaration = ktPsiFactory.createProperty(newEmptyObjectDeclaration)
        val hasStringNotNullParameter = ktClass.primaryConstructorParameters.any { parameter ->
            val type = parameter.type()
            type?.isMarkedNullable?.not() == true && KotlinBuiltIns.isString(type)
        }

        project.executeWriteCommand(COMMAND_NAME) {
            val companionObject = ktClass.getOrCreateCompanionObject()
            val companionObjectBody = companionObject.getOrCreateBody()

            companionObjectBody.addBefore(propertyDeclaration, companionObjectBody.rBrace)
            if (hasStringNotNullParameter) {
                ktClass.containingKtFile.addImportPackages(EMPTY_STRING_PROPERTY_FQN)
            }

            ktClass.reformatWithCodeStyle()
        }
    }

    private fun KtClass.getEmptyObjectPropertyDeclaration(): String {
        val emptyProperties = primaryConstructorParameters.joinToString(
            prefix = "\n",
            postfix = "\n",
            separator = ",\n"
        ) { parameter ->
            "${parameter.name} = ${parameter.getEmptyObjectValue()}"
        }

        return """
        val $EMPTY_OBJECT_PROPERTY_NAME = $name($emptyProperties) 
        """
    }

    private fun KtParameter.getEmptyObjectValue(): String {
        val parameterType = type()

        return when {
            parameterType == null || parameterType.isMarkedNullable -> "null"
            KotlinBuiltIns.isBoolean(parameterType) -> "false"
            KotlinBuiltIns.isChar(parameterType) -> "''"
            KotlinBuiltIns.isDouble(parameterType) -> "0.0"
            KotlinBuiltIns.isFloat(parameterType) -> "0f"
            KotlinBuiltIns.isInt(parameterType) || KotlinBuiltIns.isShort(parameterType) || KotlinBuiltIns.isByte(parameterType) -> "0"
            KotlinBuiltIns.isLong(parameterType) -> "0L"
            KotlinBuiltIns.isNullableAny(parameterType) -> "null"
            KotlinBuiltIns.isString(parameterType) -> "$STRING_PARAMETER_TYPE_NAME.$EMPTY_OBJECT_PROPERTY_NAME"
            KotlinBuiltIns.isListOrNullableList(parameterType) -> "emptyList()"
            KotlinBuiltIns.isSetOrNullableSet(parameterType) -> "emptySet()"
            KotlinBuiltIns.isMapOrNullableMap(parameterType) -> "emptyMap()"
            KotlinBuiltIns.isArrayOrPrimitiveArray(parameterType) -> "emptyArray()"
            KotlinBuiltIns.isCollectionOrNullableCollection(parameterType) -> "emptyList()"
            else -> "$parameterType.$EMPTY_OBJECT_PROPERTY_NAME"
        }
    }
}
