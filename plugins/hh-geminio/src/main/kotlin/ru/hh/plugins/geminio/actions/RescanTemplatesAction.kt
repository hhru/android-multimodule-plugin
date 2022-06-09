package ru.hh.plugins.geminio.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDirectory
import ru.hh.plugins.extensions.getSelectedPsiElement
import ru.hh.plugins.geminio.ActionsCreator

class RescanTemplatesAction : AnAction() {

    init {
        with(templatePresentation) {
            text = "Rescan Templates"
            icon =  com.intellij.icons.AllIcons.Actions.Refresh
            description = "Rescan folder with templates"
            isEnabledAndVisible = true
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)

        val selectedPsiElement = e.getSelectedPsiElement()
        e.presentation.isEnabledAndVisible =
            (e.project == null || selectedPsiElement == null || selectedPsiElement !is PsiDirectory).not()
    }

    override fun actionPerformed(e: AnActionEvent) {
        println("Start executing rescan templates")
        e.project?.also(ActionsCreator()::create)
    }

}