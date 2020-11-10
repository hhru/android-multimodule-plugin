package ru.hh.android.plugin.generator.steps

import com.intellij.openapi.module.Module
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.auxiliary.GrListOrMap
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrAssignmentExpression
import ru.hh.android.plugin.CodeGeneratorConstants.BUILD_GRADLE_FILE_NAME
import ru.hh.android.plugin.extensions.findPsiFileByName
import ru.hh.android.plugin.extensions.firstChildWithStartText
import ru.hh.android.plugin.model.CreateModuleConfig


class AddToothpickAnnotationProcessorOptionStep {

    companion object {
        private const val ARGUMENTS_ASSIGNMENT_NAME = "arguments"
        private const val TOOTHPICK_REGISTRY_CHILDREN_PACKAGE_NAMES_OPTION_ITEM_NAME =
                "toothpick_registry_children_package_names"
    }


    fun execute(config: CreateModuleConfig) {
        config.applications.forEach { appModuleItem ->
            modifyAnnotationProcessorOptions(appModuleItem.gradleModule, config)
        }
    }


    private fun modifyAnnotationProcessorOptions(module: Module, config: CreateModuleConfig) {
        val buildGradlePsiFile = module.findPsiFileByName(BUILD_GRADLE_FILE_NAME) ?: return

        val toothpickRegistryPsiElement = buildGradlePsiFile.originalFile
                .collectDescendantsOfType<GrAssignmentExpression>()
                .firstOrNull { it.text.startsWith(ARGUMENTS_ASSIGNMENT_NAME) }
                ?.lastChild
                ?.firstChildWithStartText(TOOTHPICK_REGISTRY_CHILDREN_PACKAGE_NAMES_OPTION_ITEM_NAME)
                ?.collectDescendantsOfType<GrListOrMap>()
                ?.first()
                ?: return

        val modifiedText = createModifiedPsiElementText(toothpickRegistryPsiElement, config.params.packageName)
        val factory = GroovyPsiElementFactory.getInstance(buildGradlePsiFile.project)
        val modifiedToothpickPsiElement = factory.createStatementFromText(modifiedText)
        val replacedElement = toothpickRegistryPsiElement.replace(modifiedToothpickPsiElement)
        CodeStyleManager.getInstance(module.project).reformat(replacedElement)
    }

    private fun createModifiedPsiElementText(toothpickRegistryPsiElement: PsiElement, packageName: String): String {
        val items = toothpickRegistryPsiElement.children
        val originalText = toothpickRegistryPsiElement.text
        val lastChildText = items.last().text
        val lastChildTextEndIndex = originalText.indexOf(lastChildText) + lastChildText.length

        return originalText.substring(0, lastChildTextEndIndex) +
                ",\n'$packageName'\n]"
    }

}