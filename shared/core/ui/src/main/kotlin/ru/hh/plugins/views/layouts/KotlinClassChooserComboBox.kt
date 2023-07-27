package ru.hh.plugins.views.layouts

import com.intellij.ide.util.ClassFilter
import com.intellij.ide.util.TreeClassChooser
import com.intellij.ide.util.TreeJavaClassChooserDialog
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.ui.layout.Row
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.idea.base.util.projectScope
import org.jetbrains.kotlin.idea.base.util.restrictToKotlinSources
import org.jetbrains.kotlin.idea.completion.extraCompletionFilter
import org.jetbrains.kotlin.idea.core.completion.DeclarationLookupObject
import org.jetbrains.kotlin.idea.core.completion.PackageLookupObject
import org.jetbrains.kotlin.idea.projectView.KtClassOrObjectTreeNode
import org.jetbrains.kotlin.psi.KtClassOrObject
import ru.hh.plugins.layout.KotlinFileComboBoxWrapper
import java.awt.event.ActionListener
import javax.swing.tree.DefaultMutableTreeNode

/**
 * Creates [ru.hh.plugins.layout.KotlinFileComboBoxWrapper] for choosing Kotlin classes with filter.
 */
fun Row.createKotlinClassChooserComboBox(
    project: Project,
    chooserDialogTitle: String,
    recentKey: String,
    initialText: String? = null,
    classFilter: ClassFilter,
    onSelectTargetClassAction: (PsiClass?, Boolean) -> Unit
): KotlinFileComboBoxWrapper {
    return KotlinFileComboBoxWrapper(
        project = project,
        browseActionListener = ActionListener {
            val chooser: TreeClassChooser = object : TreeJavaClassChooserDialog(
                chooserDialogTitle,
                project,
                project.projectScope().restrictToKotlinSources(),
                classFilter,
                null,
                null,
                false
            ) {

                override fun getSelectedFromTreeUserObject(node: DefaultMutableTreeNode): PsiClass? {
                    val psiClass = super.getSelectedFromTreeUserObject(node)
                    if (psiClass != null) {
                        return psiClass
                    }

                    val userObject = node.userObject as? KtClassOrObjectTreeNode ?: return null
                    return userObject.value.toLightClass()
                }
            }

            chooser.showDialog()

            val aClass = chooser.selected
            onSelectTargetClassAction.invoke(aClass, true)
        },
        text = initialText,
        recentsKey = recentKey
    ).also { comboBox ->
        // It should work, but it doesn't invoke lookups =(((
        comboBox.codeFragment?.extraCompletionFilter = { lookupElement ->
            when (val lookupObject = lookupElement.getObject()) {
                !is DeclarationLookupObject -> false

                is PackageLookupObject -> true

                else -> {
                    val psiElement = lookupObject.psiElement
                    psiElement is KtClassOrObject
                }
            }
        }

        comboBox.childComponent.document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                val aClass = JavaPsiFacade.getInstance(project).findClass(comboBox.text, project.projectScope())
                onSelectTargetClassAction.invoke(aClass, false)
            }
        })
    }
}
