package ru.hh.android.plugin.actions.boilerplate.empty_object

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import ru.hh.android.plugin.CodeGeneratorConstants.EMPTY_OBJECT_PROPERTY_NAME
import ru.hh.android.plugin.extensions.getKotlinDataClass
import ru.hh.android.plugin.services.code_generator.EmptyObjectGeneratorService
import ru.hh.android.plugin.utils.PluginBundle
import ru.hh.android.plugin.utils.notifyInfo

/**
 * Action for generating EMPTY object in kotlin data classes.
 */
class EmptyObjectGeneratorAction : AnAction() {

    override fun update(e: AnActionEvent) {
        super.update(e)

        val ktClass = e.getKotlinDataClass()
        if (ktClass != null) {
            e.presentation.isEnabled = ktClass.companionObjects.firstOrNull()?.findPropertyByName(EMPTY_OBJECT_PROPERTY_NAME) == null
        } else {
            e.presentation.isEnabled = false
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val kotlinDataClass = e.getKotlinDataClass() ?: return
        handleAction(e, kotlinDataClass)
    }


    private fun handleAction(e: AnActionEvent, ktClass: KtClass) {
        e.project?.service<EmptyObjectGeneratorService>()?.addEmptyObjectIntoKtClass(ktClass)
        e.project?.notifyInfo(PluginBundle.message("antiroutine.generate_empty_object.success"))
    }

}