package ru.hh.plugins.layout

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.EditorComboBox
import com.intellij.ui.RecentsManager
import com.intellij.ui.TextAccessor
import com.intellij.util.ArrayUtil
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtTypeCodeFragment
import ru.hh.plugins.extensions.EMPTY
import java.awt.event.ActionListener

/**
 * Very-very rough version of [org.jetbrains.kotlin.idea.refactoring.ui.KotlinTypeReferenceEditorComboWithBrowseButton]
 * without [com.intellij.openapi.editor.Document] stuff.
 */
class KotlinFileComboBoxWrapper(
    browseActionListener: ActionListener,
    text: String?,
    recentsKey: String,
    private val project: Project
) : ComponentWithBrowseButton<EditorComboBox>(
    EditorComboBox(createDocument(text, project), project, KotlinFileType.INSTANCE),
    browseActionListener
),
    TextAccessor {

    companion object {
        private fun createDocument(text: String?, project: Project): Document? {
            val codeFragment = KtPsiFactory(project).createTypeCodeFragment(text ?: String.EMPTY, null)
            return PsiDocumentManager.getInstance(project).getDocument(codeFragment)
        }
    }

    init {
        RecentsManager.getInstance(project).getRecentEntries(recentsKey)?.let {
            childComponent.setHistory(ArrayUtil.toStringArray(it))
        }

        if (text != null) {
            if (text.isNotEmpty()) {
                childComponent.prependItem(text)
            } else {
                childComponent.selectedItem = null
            }
        }
    }

    override fun getText() = childComponent.text.trim { it <= ' ' }

    override fun setText(text: String) {
        childComponent.text = text
    }

    val codeFragment: KtTypeCodeFragment?
        get() = PsiDocumentManager.getInstance(project).getPsiFile(childComponent.document) as? KtTypeCodeFragment
}
