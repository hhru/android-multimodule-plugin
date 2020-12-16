package ru.hh.plugins.extensions.layout

import com.intellij.ide.util.ClassFilter
import com.intellij.ide.util.TreeClassChooser
import com.intellij.ide.util.TreeJavaClassChooserDialog
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo
import com.intellij.ui.layout.Row
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.idea.completion.extraCompletionFilter
import org.jetbrains.kotlin.idea.core.completion.DeclarationLookupObject
import org.jetbrains.kotlin.idea.core.completion.PackageLookupObject
import org.jetbrains.kotlin.idea.projectView.KtClassOrObjectTreeNode
import org.jetbrains.kotlin.idea.refactoring.ui.KotlinDestinationFolderComboBox
import org.jetbrains.kotlin.idea.search.projectScope
import org.jetbrains.kotlin.idea.search.restrictToKotlinSources
import org.jetbrains.kotlin.psi.KtClassOrObject
import ru.hh.plugins.layout.KotlinFileComboBoxWrapper
import java.awt.event.ActionListener
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.tree.DefaultMutableTreeNode


private const val MINIMUM_GAP_FOR_COMBO_BOX_IN_PX = 5
private const val MIN_COMBO_BOX_WIDTH_IN_PX = 40


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

/**
 * Creates [com.intellij.refactoring.ui.PackageNameReferenceEditorCombo] with default package name.
 */
fun Row.targetPackageComboBox(
    project: Project,
    initialText: String,
    recentPackageKey: String,
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

fun Row.kotlinDestinationFolderComboBox(
    project: Project,
    initialPsiDirectory: PsiDirectory,
    packageNameChooserComboBox: PackageNameReferenceEditorCombo
): KotlinDestinationFolderComboBox {
    val destinationFolderComboBox = object : KotlinDestinationFolderComboBox() {
        override fun getTargetPackage(): String {
            return packageNameChooserComboBox.text.trim()
        }
    }

    destinationFolderComboBox.setData(
        project,
        initialPsiDirectory,
        packageNameChooserComboBox.childComponent
    )

    return destinationFolderComboBox
}


/**
 * Build button for files choosing.
 *
 * @param project -- current project
 * @param buttonText -- text on button that will show dialog
 * @param filterText -- text in your file chooser dialog
 * @param fileChooserButtonText -- text on approve button in dialog
 * @param filterFilesExtensions -- extensions for filtering your files (without ".", e.g. "yaml")
 * @param approveAction -- action that will be invoked, if user choose something
 */
fun Row.fileChooserButton(
    project: Project,
    buttonText: String,
    filterText: String,
    fileChooserButtonText: String,
    vararg filterFilesExtensions: String,
    approveAction: (File) -> Unit
) {
    button(buttonText) {
        val fileChooser = JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            fileFilter = FileNameExtensionFilter(filterText, *filterFilesExtensions)
            project.basePath?.let { currentDirectory = File(it) }
        }

        val result = fileChooser.showDialog(null, fileChooserButtonText)
        if (result == JFileChooser.APPROVE_OPTION) {
            approveAction.invoke(fileChooser.selectedFile)
        }
    }
}