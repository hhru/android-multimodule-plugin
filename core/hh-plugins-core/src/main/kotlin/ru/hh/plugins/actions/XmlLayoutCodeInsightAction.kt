package ru.hh.plugins.actions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import ru.hh.plugins.PluginsConstants


/**
 * Base action for generating code from XML layout file.
 */
abstract class XmlLayoutCodeInsightAction : XmlCodeInsightAction() {

    final override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return file is XmlFile && file.parent?.name == PluginsConstants.ANDROID_LAYOUT_RES_DIR_NAME
    }

}