package ru.hh.android.plugins.garcon.actions.rv_item_page_object.dialog

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.refactoring.util.RefactoringMessageUtil
import com.intellij.ui.EditorTextField
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import org.jetbrains.kotlin.asJava.classes.KtLightClassForSourceDeclaration
import ru.hh.android.plugins.garcon.GarconEventLogger
import ru.hh.android.plugins.garcon.extensions.base_types.EMPTY
import ru.hh.android.plugins.garcon.extensions.base_types.isValidIdentifier
import ru.hh.android.plugins.garcon.extensions.base_types.layoutFileNameToClassName
import ru.hh.android.plugins.garcon.extensions.kakaoScreensClassFilter
import ru.hh.android.plugins.garcon.extensions.layout.createKotlinClassChooserComboBox
import ru.hh.android.plugins.garcon.extensions.layout.editorTextField
import ru.hh.android.plugins.garcon.extensions.showErrorMessage
import ru.hh.android.plugins.garcon.utils.GarconBundle
import ru.hh.android.plugins.garcon.views.KotlinFileComboBoxWrapper
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel


class GenerateRVItemPageObjectDialog(
    private val xmlFile: XmlFile
) : DialogWrapper(xmlFile.project, true) {

    companion object {
        private const val TAG = "GenerateRVItemPageObjectDialog"

        private const val KEY_OPEN_IN_EDITOR = "$TAG.OpenInEditor"
        private const val KEY_RECENT_TARGET_CLASS = "$TAG.RecentTargetClass"
    }


    private val project: Project
        get() = xmlFile.project

    private val eventLogger: GarconEventLogger by lazy {
        project.getComponent(GarconEventLogger::class.java)
    }


    private lateinit var rvItemPageObjectName: EditorTextField
    private lateinit var openInEditorCheckBox: JCheckBox
    private lateinit var targetClassChooser: KotlinFileComboBoxWrapper

    private var targetClass: PsiElement? = null


    init {
        init()
        title = GarconBundle.message("garcon.forms.generate_rv_page_object.title")
    }


    override fun createCenterPanel(): JComponent? {
        return JPanel(BorderLayout())
    }

    @Suppress("UnstableApiUsage")
    override fun createNorthPanel(): JComponent? {
        return panel {
            row {
                label(
                    text = GarconBundle.message("garcon.forms.generate_rv_page_object.label.0", xmlFile.name),
                    bold = true
                )
            }

            row(GarconBundle.message("garcon.forms.generate_rv_page_object.class_name")) {
                rvItemPageObjectName = editorTextField(
                    text = xmlFile.name.layoutFileNameToClassName(),
                    selectAll = true
                )
                rvItemPageObjectName(CCFlags.growX)
            }

            row(GarconBundle.message("garcon.common.forms.destination_class")) {
                targetClassChooser = initTargetClassChooser()
                targetClassChooser(CCFlags.growX)
            }

            row {
                openInEditorCheckBox = checkBox(
                    text = GarconBundle.message("garcon.common.forms.open_in_editor"),
                    isSelected = PropertiesComponent.getInstance().getBoolean(KEY_OPEN_IN_EDITOR, true)
                )
                openInEditorCheckBox(CCFlags.pushX)
            }
        }
    }

    override fun doOKAction() {
        if (checkClassNameIsValid() && checkTargetClassIsValid()) {
            saveOpenInEditorFlag(getOpenInEditor())
            super.doOKAction()
        }

        return
    }


    fun getPageObjectClassName(): String {
        return rvItemPageObjectName.text
    }

    fun getOpenInEditor(): Boolean {
        return openInEditorCheckBox.isSelected
    }

    fun getTargetClass(): PsiElement? {
        return targetClass
    }


    private fun initTargetClassChooser(): KotlinFileComboBoxWrapper {
        return createKotlinClassChooserComboBox(
            project = project,
            chooserDialogTitle = GarconBundle.message("garcon.dialogs.choose_destination_class"),
            initialText = null,
            recentKey = KEY_RECENT_TARGET_CLASS,
            classFilter = kakaoScreensClassFilter(),
            onSelectTargetClassAction = { aClass, needChangeText ->
                if (aClass is KtLightClassForSourceDeclaration) {
                    targetClass = aClass.kotlinOrigin
                    if (needChangeText) {
                        targetClassChooser.text = aClass.qualifiedName ?: String.EMPTY
                    }
                } else {
                    targetClass = aClass
                }
            }
        )
    }

    private fun checkClassNameIsValid(): Boolean {
        val className = getPageObjectClassName()

        return when {
            className.isEmpty() -> {
                showClassNameError(GarconBundle.message("garcon.errors.no_class_name"))
                false
            }

            className.isValidIdentifier(project).not() -> {
                showClassNameError(RefactoringMessageUtil.getIncorrectIdentifierMessage(className))
                false
            }

            else -> {
                true
            }
        }
    }

    private fun showClassNameError(errorMessage: String) {
        eventLogger.error(errorMessage)
        showErrorMessage(
            project,
            errorMessage,
            rvItemPageObjectName
        )
    }

    private fun checkTargetClassIsValid(): Boolean {
        return if (targetClass == null) {
            showTargetClassError(GarconBundle.message("garcon.errors.no_target_class"))
            false
        } else {
            true
        }
    }

    private fun showTargetClassError(errorMessage: String) {
        eventLogger.error(errorMessage)
        showErrorMessage(
            project,
            errorMessage,
            targetClassChooser
        )
    }

    private fun saveOpenInEditorFlag(currentValue: Boolean) {
        PropertiesComponent.getInstance().setValue(KEY_OPEN_IN_EDITOR, currentValue.toString())
    }

}