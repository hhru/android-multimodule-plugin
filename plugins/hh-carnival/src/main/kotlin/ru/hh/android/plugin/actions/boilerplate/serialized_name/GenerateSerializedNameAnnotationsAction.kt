package ru.hh.android.plugin.actions.boilerplate.serialized_name

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.actions.generate.KotlinGenerateActionBase
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import ru.hh.android.plugin.services.code_generator.SerializedNameAnnotationsGeneratorService
import ru.hh.plugins.logger.HHNotifications

class GenerateSerializedNameAnnotationsAction : KotlinGenerateActionBase() {

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        (getTargetClass(editor, file) as? KtClass)?.let { targetClass ->
            SerializedNameAnnotationsGeneratorService.getInstance(project)
                .addSerializedNameAnnotationsIntoClass(targetClass)
            HHNotifications.info("@SerializedName annotations successfully generated!")
        }
    }

    override fun isValidForClass(targetClass: KtClassOrObject): Boolean {
        return targetClass is KtClass && targetClass.isData()
    }
}
