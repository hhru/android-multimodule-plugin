package ru.hh.android.plugin.actions.boilerplate.serialized_name

import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.psi.KtClass
import ru.hh.android.plugin.actions.KotlinDataClassAction
import ru.hh.android.plugin.extensions.getKotlinDataClass


class AddSerializedNameAnnotationsAction : KotlinDataClassAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val kotlinDataClass = e.getKotlinDataClass() ?: return
        handleAction(kotlinDataClass)
    }


    private fun handleAction(ktClass: KtClass) {
        // todo
    }

}