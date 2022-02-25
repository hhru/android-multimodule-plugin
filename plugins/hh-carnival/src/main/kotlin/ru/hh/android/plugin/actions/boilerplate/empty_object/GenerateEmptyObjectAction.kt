package ru.hh.android.plugin.actions.boilerplate.empty_object

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.actions.generate.KotlinGenerateActionBase
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import ru.hh.android.plugin.CodeGeneratorConstants
import ru.hh.android.plugin.services.code_generator.EmptyObjectGeneratorService
import ru.hh.android.plugin.utils.notifyInfo

/**
 * Action for generating EMPTY object in kotlin data classes.
 */
class GenerateEmptyObjectAction : KotlinGenerateActionBase() {

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val targetClass = getTargetClass(editor, file) as? KtClass
        targetClass?.let { ktClass ->
            EmptyObjectGeneratorService.getInstance(project).addEmptyObjectIntoKtClass(ktClass)
            project.notifyInfo("EMPTY object successfully generated!")
        }
    }

    override fun isValidForClass(targetClass: KtClassOrObject): Boolean {
        return targetClass is KtClass &&
            targetClass.isData() &&
            (
                targetClass.companionObjects.firstOrNull()
                    ?.findPropertyByName(CodeGeneratorConstants.EMPTY_OBJECT_PROPERTY_NAME) == null
                )
    }
}
