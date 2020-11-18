package ru.hh.plugins.actions

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile


/**
 * Base action for generating code from some XML file.
 */
abstract class XmlCodeInsightAction : CodeInsightAction(), CodeInsightActionHandler {

    abstract fun handleAction(project: Project, editor: Editor, psiFile: PsiFile)


    final override fun startInWriteAction(): Boolean = false

    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return file is XmlFile
    }


    final override fun getHandler(): CodeInsightActionHandler = this

    final override fun invoke(project: Project, editor: Editor, psiFile: PsiFile) {
        handleAction(project, editor, psiFile)
    }

}