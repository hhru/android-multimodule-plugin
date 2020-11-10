package ru.hh.android.plugins.garcon.extensions.layout

import com.intellij.ide.util.ClassFilter
import com.intellij.ide.util.TreeClassChooser
import com.intellij.ide.util.TreeJavaClassChooserDialog
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pass
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.refactoring.move.moveClassesOrPackages.DestinationFolderComboBox
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo
import com.intellij.ui.EditorTextField
import com.intellij.ui.ReferenceEditorComboWithBrowseButton
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.idea.completion.extraCompletionFilter
import org.jetbrains.kotlin.idea.core.completion.DeclarationLookupObject
import org.jetbrains.kotlin.idea.core.completion.PackageLookupObject
import org.jetbrains.kotlin.idea.projectView.KtClassOrObjectTreeNode
import org.jetbrains.kotlin.idea.search.projectScope
import org.jetbrains.kotlin.idea.search.restrictToKotlinSources
import org.jetbrains.kotlin.psi.KtClassOrObject
import ru.hh.android.plugins.garcon.extensions.base_types.SPACE
import ru.hh.android.plugins.garcon.views.KotlinFileComboBoxWrapper
import java.awt.event.ActionListener
import javax.swing.tree.DefaultMutableTreeNode


private const val MINIMUM_GAP_FOR_COMBO_BOX_IN_PX = 5
private const val MIN_COMBO_BOX_WIDTH_IN_PX = 40

/**
 * Creates [com.intellij.ui.EditorTextField] with text and selection.
 */
fun editorTextField(text: String, selectAll: Boolean = false): EditorTextField {
    return EditorTextField(text).apply {
        if (selectAll) {
            this.selectAll()
        }
    }
}

/**
 * Creates [com.intellij.refactoring.ui.PackageNameReferenceEditorCombo] with default package name.
 */
fun targetPackageComboBox(
    project: Project,
    recentPackageKey: String,
    initialText: String,
    labelText: String
): PackageNameReferenceEditorCombo {
    return PackageNameReferenceEditorCombo(
        initialText,
        project,
        recentPackageKey,
        labelText
    ).apply {
        val preferredWidth = (initialText.length + MINIMUM_GAP_FOR_COMBO_BOX_IN_PX)
            .coerceAtLeast(MIN_COMBO_BOX_WIDTH_IN_PX)
        setTextFieldPreferredWidth(preferredWidth)
    }
}

/**
 * Creates [com.intellij.refactoring.move.moveClassesOrPackages.DestinationFolderComboBox] bounded with
 * [com.intellij.refactoring.ui.PackageNameReferenceEditorCombo], initialized with [com.intellij.psi.PsiDirectory].
 */
fun targetFolderComboBox(
    project: Project,
    targetPackageComboBox: ReferenceEditorComboWithBrowseButton,
    initialPsiDirectory: PsiDirectory? = null,
    onError: (String?, DestinationFolderComboBox) -> Unit = { _, _ -> }
): DestinationFolderComboBox {
    return object : DestinationFolderComboBox() {
        override fun getTargetPackage(): String {
            return targetPackageComboBox.text.trim { it <= Char.SPACE }
        }

        override fun reportBaseInTestSelectionInSource(): Boolean {
            return true
        }
    }.also { comboBox ->
        comboBox.setData(
            project,
            initialPsiDirectory,
            object : Pass<String?>() {
                override fun pass(s: String?) {
                    onError.invoke(s, comboBox)
                }
            },
            targetPackageComboBox.childComponent
        )
    }
}

/**
 * Creates [ru.hh.android.plugins.garcon.views.KotlinFileComboBoxWrapper] for choosing Kotlin classes with filter.
 */
fun createKotlinClassChooserComboBox(
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
        // TODO -- It should work, but it doesn't invoke lookups =(((
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