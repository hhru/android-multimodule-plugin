package ru.hh.android.plugin.actions.boilerplate.serialized_name

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import org.jetbrains.kotlin.psi.KtClass
import ru.hh.android.plugin.actions.KotlinDataClassAction
import ru.hh.android.plugin.extensions.getKotlinDataClass
import ru.hh.android.plugin.services.code_generator.SerializedNameAnnotationsGeneratorService
import ru.hh.android.plugin.utils.PluginBundle
import ru.hh.android.plugin.utils.notifyInfo


class GenerateSerializedNameAnnotationsAction : KotlinDataClassAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val kotlinDataClass = e.getKotlinDataClass() ?: return
        handleAction(kotlinDataClass)
    }


    private fun handleAction(ktClass: KtClass) {
        with(ktClass.project) {
            service<SerializedNameAnnotationsGeneratorService>().addSerializedNameAnnotationsIntoClass(ktClass)
            notifyInfo(PluginBundle.message("antiroutine.generate_serialized_name.success"))
        }
    }

}